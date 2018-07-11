package ru.kabylin.andrey.vocabulator.compositors

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

class RetryWithDelayCompositor(
    private val maxRetries: Int = -1,
    private val retryDelay: Long,
    private val timeUnit: TimeUnit,
    private val predicate: (Throwable) -> Boolean = { true }
) : Compositor {
    private var retryCount: Int = 0

    private fun matchError(t: Throwable) = !predicate(t)

    private fun matchTimer(t: Throwable) = maxRetries == -1 || ++retryCount <= maxRetries

    override fun <T> compose(single: Single<T>): Single<T> =
        single.retryWhen {
            it.flatMap {
                when {
                    matchError(it) -> Flowable.error<T>(it)
                    matchTimer(it) -> Flowable.timer(retryDelay, timeUnit)
                    else -> Flowable.error<T>(it)
                }
            }
        }

    override fun <T> compose(flowable: Flowable<T>): Flowable<T> =
        flowable.retryWhen {
            it.flatMap {
                when {
                    matchError(it) -> Flowable.error<T>(it)
                    matchTimer(it) -> Flowable.timer(retryDelay, timeUnit)
                    else -> Flowable.error<T>(it)
                }
            }
        }

    override fun <T> compose(observable: Observable<T>): Observable<T> =
        observable.retryWhen {
            it.flatMap {
                when {
                    matchError(it) -> Observable.error<T>(it)
                    matchTimer(it) -> Observable.timer(retryDelay, timeUnit)
                    else -> Observable.error<T>(it)
                }
            }
        }

    override fun compose(completable: Completable): Completable =
        completable.retryWhen {
            it.flatMap {
                when {
                    matchError(it) -> Flowable.error(it)
                    matchTimer(it) -> Flowable.timer(retryDelay, timeUnit)
                    else -> Flowable.error(it)
                }
            }
        }
}
