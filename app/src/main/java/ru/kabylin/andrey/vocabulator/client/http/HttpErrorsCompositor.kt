package ru.kabylin.andrey.vocabulator.client.http

import com.google.gson.JsonSyntaxException
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.HttpException
import ru.kabylin.andrey.vocabulator.client.*
import ru.kabylin.andrey.vocabulator.compositors.Compositor
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Данный класс перемапливает исключения [HttpException]
 * в исключения из ru/mass/client/errors.kt
 */
class HttpErrorsCompositor(client: HttpClient) : Compositor {
    private val errorResponseConverter = ErrorsConverter(client.dataState)

    override fun <T> compose(single: Single<T>): Single<T> =
        // Даем возможность переопределять onErrorResumeNext.
        Single.just(Unit)
            .flatMap { single }
            .onErrorResumeNext { throwable: Throwable ->
                Single.error(remapExceptions(errorResponseConverter, throwable))
            }

    override fun <T> compose(flowable: Flowable<T>): Flowable<T> {
        return flowable.onErrorResumeNext { throwable: Throwable ->
            Flowable.error(remapExceptions(errorResponseConverter, throwable))
        }
    }

    override fun <T> compose(observable: Observable<T>): Observable<T> {
        return observable.onErrorResumeNext { throwable: Throwable ->
            Observable.error(remapExceptions(errorResponseConverter, throwable))
        }
    }

    override fun compose(completable: Completable): Completable {
        return completable.onErrorResumeNext { throwable: Throwable ->
            Completable.error(remapExceptions(errorResponseConverter, throwable))
        }
    }

    companion object {
        fun remapExceptions(errorResponseConverter: ErrorsConverter, throwable: Throwable): Throwable =
            when (throwable) {
                is ConnectException -> AccessError(AccessErrorReason.LOST_CONNECTION, throwable)
                is SocketException -> AccessError(AccessErrorReason.LOST_CONNECTION, throwable)
                is UnknownHostException -> AccessError(AccessErrorReason.LOST_CONNECTION, throwable)
                is SocketTimeoutException -> AccessError(AccessErrorReason.TIMEOUT, throwable)
                is JsonSyntaxException -> AccessError(AccessErrorReason.BAD_RESPONSE, throwable)
                is HttpException -> parseHttpError(errorResponseConverter, throwable)
                else -> throwable
            }

        private fun parseHttpError(errorResponseConverter: ErrorsConverter, httpException: HttpException): Throwable {
            val response = httpException.response()
            val statusCode = response.code()
            val json = response.errorBody()?.string()

            try {
                if (json != null)
                    return errorResponseConverter.convert(json)
            } catch (e: Exception) {
            }

            return when (statusCode) {
                400 -> AccessError(AccessErrorReason.BAD_RESPONSE, httpException)
                404 -> AccessError(AccessErrorReason.NOT_FOUND, httpException)
                401 -> SessionError("Unauthorized", httpException)
                403 -> {
                    try {
                        val error = errorResponseConverter.parseResponse(json!!)
                        val details = error["info"] as String
                        val code = error["code"] as String
                        val message = "Доступ запрещен\n$code: $details"

                        CredentialsError(message, httpException)
                    } catch (t: Throwable) {
                        CredentialsError("Доступ запрещен", httpException)
                    }
                }
                405 -> AccessError("Access error: Method not allowed", httpException)
                423 -> AccessError("Access error: Locked", httpException)
                429 -> AccessError(AccessErrorReason.TOO_MANY_REQUESTS, httpException)
                500 -> AccessError(AccessErrorReason.INTERNAL_SERVER_ERROR, httpException)
                else -> AccessError(AccessErrorReason.BAD_RESPONSE, httpException)
            }
        }
    }
}
