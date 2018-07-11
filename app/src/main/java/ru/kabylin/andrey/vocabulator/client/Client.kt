package ru.kabylin.andrey.vocabulator.client

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.kabylin.andrey.vocabulator.compositors.Compositor
import ru.kabylin.andrey.vocabulator.compositors.ErrorsCompositor
import ru.kabylin.andrey.vocabulator.compositors.MergeCompositor
import ru.kabylin.andrey.vocabulator.compositors.RequestStateCompositor
import java.util.concurrent.LinkedBlockingQueue

enum class RequestState {
    STARTED,
    FINISHED
}

class ClientRequest(
    val requestCode: Int = Client.REQUEST_DEFAULT,
    val executor: ClientRequest.() -> Unit,
    internal var disposable: Disposable?,
    val onDispose: (() -> Unit)? = null
) {
    constructor(requestCode: Int = Client.REQUEST_DEFAULT, executor: ClientRequest.() -> Unit)
        : this(requestCode, executor, null)

    private var isDone = false

    fun markAsDone() {
        isDone = true
    }

    fun isDone() = isDone

    fun dispose() {
        disposable?.dispose()

        if (disposable != null) {
            println("DISPOSE!")
        } else {
            println("TRIED BUT NULL :(")
        }
    }

    fun isDisposed(): Boolean =
        disposable?.isDisposed ?: true
}

data class ClientResponse<out T>(
    val payload: T,
    val requestCode: Int = Client.REQUEST_DEFAULT,
    val success: Boolean
)

open class Client {
    companion object {
        const val REQUEST_DEFAULT = 2000

        fun createErrorResponse(reason: DescReason): ClientResponse<LogicError> =
            ClientResponse(LogicError(reason), REQUEST_DEFAULT, false)
    }

    val logicErrors: Subject<ClientResponse<LogicError>> = PublishSubject.create()
    val validationErrors: Subject<ClientResponse<ValidationErrors>> = PublishSubject.create()
    val criticalErrors: Subject<ClientResponse<Throwable>> = PublishSubject.create()
    val credentialsError: Subject<ClientResponse<CredentialsError>> = PublishSubject.create()
    val accessErrors: Subject<ClientResponse<AccessError>> = PublishSubject.create()

    val requestState: Subject<ClientResponse<RequestState>> = PublishSubject.create()
    val requests: Subject<ClientRequest> = PublishSubject.create()
    val errors: Subject<ClientResponse<Throwable>> = PublishSubject.create()

    val queue = LinkedBlockingQueue<ClientRequest>()

    var compositor: Compositor? = null
        set(value) {
            assert(field == null)
            field = value
        }

    private var lastRequest: ClientRequest? = null
    private var lastError: Throwable? = null

    // Текущие не выполненные запросы
    private val currentRequests = ArrayList<ClientRequest>()

    fun subscriberOnErrors(listener: ErrorsListener): ErrorsSubscriber =
        ErrorsSubscriber(
            logicErrors.subscribe(listener::onLogicError),
            validationErrors.subscribe(listener::onValidationErrors),
            criticalErrors.subscribe(listener::onCriticalError),
            credentialsError.subscribe(listener::onCredentialsError),
            accessErrors.subscribe(listener::onAccessError),
            errors.subscribe(listener::onError)
        )

    fun retryLastRequest() {
        assert(lastRequest != null) { "lastRequest can't be null" }
        executeRequest(lastRequest!!)
    }

    private fun executeRequest(request: ClientRequest) {
        try {
            for (currentRequest in currentRequests) {
                if (currentRequest.requestCode != REQUEST_DEFAULT && request.requestCode == currentRequest.requestCode) {
                    currentRequest.dispose()
                }
            }

            currentRequests.removeAll { it.isDisposed() || it.isDone() }
            val executor = request.executor
            request.executor()
            currentRequests.add(request)
        } catch (throwable: Exception) {
            onError(throwable, request.requestCode)
        }
    }

    fun execute(request: ClientRequest) {
        requests.onNext(request)
        executeRequest(request)
        queue.add(request)
        lastRequest = request
    }

    fun <T> execute(query: Single<T>, onSuccess: (ClientResponse<T>) -> Unit = {}): ClientRequest =
        execute(query, Client.REQUEST_DEFAULT, onSuccess)

    fun execute(query: Completable, onComplete: () -> Unit = {}): ClientRequest =
        execute(query, Client.REQUEST_DEFAULT, onComplete)

    fun <T> execute(query: Flowable<T>, onNext: (ClientResponse<T>) -> Unit = {}): ClientRequest =
        execute(query, Client.REQUEST_DEFAULT, onNext, {})

    fun <T> execute(query: Flowable<T>, onNext: (ClientResponse<T>) -> Unit, onComplete: () -> Unit): ClientRequest =
        execute(query, Client.REQUEST_DEFAULT, onNext, onComplete)

    fun <T> execute(query: Observable<T>, onNext: (ClientResponse<T>) -> Unit, onComplete: () -> Unit = {}): ClientRequest =
        execute(query, Client.REQUEST_DEFAULT, onNext, onComplete)

    fun <T> execute(query: Single<T>, requestCode: Int, onSuccess: (ClientResponse<T>) -> Unit = {}): ClientRequest {
        val clientRequest = RequestBuilder()
            .withQueue(queue)
            .withRequestCode(requestCode)
            .withCompositor(createRequestCompositor(requestCode))
            .buildForSingle(query, onSuccess)

        execute(clientRequest)
        return clientRequest
    }

    fun execute(query: Completable, requestCode: Int, onComplete: () -> Unit): ClientRequest {
        val clientRequest = RequestBuilder()
            .withRequestCode(requestCode)
            .withCompositor(createRequestCompositor(requestCode))
            .buildForCompletable(query, onComplete)

        execute(clientRequest)
        return clientRequest
    }

    fun <T> execute(query: Flowable<T>, requestCode: Int, onNext: (ClientResponse<T>) -> Unit, onComplete: () -> Unit): ClientRequest {
        val clientRequest = RequestBuilder()
            .withRequestCode(requestCode)
            .withCompositor(createRequestCompositor(requestCode))
            .buildForFlowable(query, onNext, onComplete)

        execute(clientRequest)
        return clientRequest
    }

    fun <T> execute(query: Observable<T>, requestCode: Int, onNext: (ClientResponse<T>) -> Unit, onComplete: () -> Unit): ClientRequest {
        val clientRequest = RequestBuilder()
            .withRequestCode(requestCode)
            .withCompositor(createRequestCompositor(requestCode))
            .buildForObservable(query, onNext, onComplete)

        execute(clientRequest)
        return clientRequest
    }

    private fun createRequestCompositor(requestCode: Int = Client.REQUEST_DEFAULT): Compositor {
        val requestStateCompositor = RequestStateCompositor(this, requestCode)
        val errorsCompositor = ErrorsCompositor(this, requestCode)
        val requestCompositor = MergeCompositor(requestStateCompositor, errorsCompositor)

        return if (compositor == null) {
            requestCompositor
        } else {
            MergeCompositor(compositor!!, requestCompositor)
        }
    }

    fun onError(throwable: Throwable, requestCode: Int = Client.REQUEST_DEFAULT) {
        throwable.printStackTrace()

        fun <T> response(t: T): ClientResponse<T> {
            return ClientResponse(t, requestCode, false)
        }

        if (throwable !is LogicError && throwable !is SessionError)
            disposeAndClearQueue()

        when (throwable) {
            is LogicError -> logicErrors.onNext(response(throwable))
            is CredentialsError -> credentialsError.onNext(response(throwable))
            is AccessError -> {
                accessErrors.onNext(response(throwable))

                if (throwable.reason == AccessErrorReason.BAD_RESPONSE) {
                    logException(throwable)
                }

                if (throwable.reason is AccessErrorReason.UNSPECIFIED) {
                    logException(throwable)
                }
            }
            is ValidationErrors -> validationErrors.onNext(response(throwable))
            is SessionError -> errors.onNext(response(throwable))
            is CanceledError -> { /* Nothing */ }
            else -> {
                criticalErrors.onNext(response(throwable))
                logException(throwable)
            }
        }

        errors.onNext(response(throwable))
    }

    private fun logException(throwable: Throwable) {
        // Тут обычно крашлитик, но его в этом приложении нету :)
    }

    fun disposeLast() {
        lastRequest?.dispose()
    }

    fun disposeAndClearQueue() {
        for (item in queue)
            item.dispose()

        queue.clear()
    }

    fun resetQueue() {
        for (item in queue)
            item.dispose()

        for (item in queue)
            execute(item)
    }
}

interface ErrorsListener {
    fun onLogicError(error: ClientResponse<LogicError>) {}
    fun onValidationErrors(error: ClientResponse<ValidationErrors>) {}
    fun onCredentialsError(error: ClientResponse<CredentialsError>) {}
    fun onAccessError(error: ClientResponse<AccessError>) {}
    fun onCriticalError(error: ClientResponse<Throwable>) {}
    fun onError(error: ClientResponse<Throwable>) {}
}

data class ErrorsSubscriber(
    val logicErrors: Disposable? = null,
    val validationErrors: Disposable? = null,
    val criticalErrors: Disposable? = null,
    val credentialsError: Disposable? = null,
    val accessErrors: Disposable? = null,
    val errors: Disposable? = null
) : Disposable {

    private fun isErrorDisposed(disposable: Disposable?): Boolean =
        disposable?.isDisposed ?: true

    override fun isDisposed(): Boolean =
        isErrorDisposed(logicErrors) &&
            isErrorDisposed(validationErrors) &&
            isErrorDisposed(criticalErrors) &&
            isErrorDisposed(credentialsError) &&
            isErrorDisposed(accessErrors) &&
            isErrorDisposed(errors)

    override fun dispose() {
        logicErrors?.dispose()
        validationErrors?.dispose()
        criticalErrors?.dispose()
        credentialsError?.dispose()
        accessErrors?.dispose()
        errors?.dispose()
    }
}
