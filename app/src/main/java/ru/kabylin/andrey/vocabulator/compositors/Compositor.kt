package ru.kabylin.andrey.vocabulator.compositors

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Композитор - интерфейс, который определяет различные стратегии композиции.
 * К примеру стандартная компоизиция может быть - выполнять задачу в новом поток,
 * результат отправить в UI поток.
 */
interface Compositor {
    fun <T> compose(single: Single<T>): Single<T>

    fun <T> compose(flowable: Flowable<T>): Flowable<T>

    fun <T> compose(observable: Observable<T>): Observable<T>

    fun compose(completable: Completable): Completable
}
