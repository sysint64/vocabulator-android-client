package ru.kabylin.andrey.vocabulator.compositors

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.client.*

class ErrorsCompositor(
    val client: Client,
    private val requestCode: Int = Client.REQUEST_DEFAULT
) : Compositor {

    override fun <T> compose(single: Single<T>): Single<T> {
        return single.doOnError(this::onError)
    }

    override fun <T> compose(flowable: Flowable<T>): Flowable<T> {
        return flowable.doOnError(this::onError)
    }

    override fun <T> compose(observable: Observable<T>): Observable<T> {
        return observable.doOnError(this::onError)
    }

    override fun compose(completable: Completable): Completable {
        return completable.doOnError(this::onError)
    }

    private fun onError(throwable: Throwable) {
        client.onError(throwable, requestCode)
    }
}
