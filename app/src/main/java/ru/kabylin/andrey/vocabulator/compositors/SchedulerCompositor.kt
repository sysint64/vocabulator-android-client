package ru.kabylin.andrey.vocabulator.compositors

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerCompositor(
    private val backgroundScheduler: Scheduler = Schedulers.io(),
    private val resultScheduler: Scheduler = AndroidSchedulers.mainThread()
) : Compositor {
    override fun <T> compose(single: Single<T>): Single<T> {
        return single
            .subscribeOn(backgroundScheduler)
            .observeOn(resultScheduler)
    }

    override fun <T> compose(flowable: Flowable<T>): Flowable<T> {
        return flowable
            .subscribeOn(backgroundScheduler)
            .observeOn(resultScheduler)
    }

    override fun <T> compose(observable: Observable<T>): Observable<T> {
        return observable
            .subscribeOn(backgroundScheduler)
            .observeOn(resultScheduler)
    }

    override fun compose(completable: Completable): Completable {
        return completable
            .subscribeOn(backgroundScheduler)
            .observeOn(resultScheduler)
    }
}
