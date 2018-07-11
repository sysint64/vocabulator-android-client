package ru.kabylin.andrey.vocabulator.compositors

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

class MergeCompositor(private val top: Compositor, private val bottom: Compositor) : Compositor {
    override fun <T> compose(single: Single<T>): Single<T> {
        return bottom.compose(top.compose(single))
    }

    override fun <T> compose(flowable: Flowable<T>): Flowable<T> {
        return bottom.compose(top.compose(flowable))
    }

    override fun <T> compose(observable: Observable<T>): Observable<T> {
        return bottom.compose(top.compose(observable))
    }

    override fun compose(completable: Completable): Completable {
        return bottom.compose(top.compose(completable))
    }

}
