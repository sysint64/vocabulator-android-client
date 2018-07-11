package ru.kabylin.andrey.vocabulator.compositors

import io.reactivex.*

abstract class ConfigCompositor : Compositor {
    abstract val config: Compositor

    override fun <T> compose(single: Single<T>): Single<T> {
        return config.compose(single)
    }

    override fun <T> compose(flowable: Flowable<T>): Flowable<T> {
        return config.compose(flowable)
    }

    override fun <T> compose(observable: Observable<T>): Observable<T> {
        return config.compose(observable)
    }

    override fun compose(completable: Completable): Completable {
        return config.compose(completable)
    }
}
