package ru.kabylin.andrey.vocabulator.ext

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun <T> Single<T>.subscribeOnSuccess(onSuccess: (T) -> Unit): Disposable =
    subscribe(onSuccess, {})

fun Completable.subscribeOnComplete(onComplete: () -> Unit): Disposable =
    subscribe(onComplete, {})

fun <T> Flowable<T>.subscribeOnNext(onNext: (T) -> Unit): Disposable =
    subscribe(onNext, {})

fun Disposable.disposeBy(disposables: CompositeDisposable) {
    disposables.add(this)
}

fun <A, B> Single<B>.chainResult(value: A): Single<Pair<A, B>> {
    return map { Pair(value, it) }
}

fun <A, B, C> Single<C>.chainResult(value: Pair<A, B>): Single<Triple<A, B, C>> {
    return map { Triple(value.first, value.second, it) }
}

inline fun <T, A, B> Single<T>.chainResult(value: A, crossinline transformer: (T) -> B): Single<Pair<A, B>> {
    return map { Pair(value, transformer(it)) }
}

inline fun <T, A, B, C> Single<T>.chainResult(value: Pair<A, B>, crossinline transformer: (T) -> C): Single<Triple<A, B, C>> {
    return map { Triple(value.first, value.second, transformer(it)) }
}

inline fun <T> Single<T>.flatMapPassCurrent(crossinline mapper: (T) -> Single<*>): Single<T> {
    return flatMap { res ->
        mapper(res).map { res }
    }
}

inline fun <T> Flowable<T>.flatMapPassCurrent(crossinline mapper: (T) -> Flowable<*>): Flowable<T> {
    return flatMap { res ->
        mapper(res).map { res }
    }
}

inline fun <T> Single<T>.flatMapPassCurrentCompletable(crossinline mapper: (T) -> Completable): Single<T> {
    return flatMap { res ->
        mapper(res).toSingle { res }
    }
}

inline fun <A, B> Single<A>.flatMapChainResult(crossinline mapper: (A) -> Single<B>): Single<Pair<A, B>> {
    return flatMap { res ->
        mapper(res).map { Pair(res, it) }
    }
}

inline fun <A, B, C> Single<Pair<A, B>>.flatMapChainResultPair(crossinline mapper: (Pair<A, B>) -> Single<C>): Single<Triple<A, B, C>> {
    return flatMap { res ->
        mapper(res).map { Triple(res.first, res.second, it) }
    }
}

inline fun <A, B> Flowable<A>.flatMapChainResult(crossinline mapper: (A) -> Flowable<B>): Flowable<Pair<A, B>> {
    return flatMap { res ->
        mapper(res).map { Pair(res, it) }
    }
}

inline fun <A, B> Flowable<A>.concatMapChainResult(crossinline mapper: (A) -> Flowable<B>): Flowable<Pair<A, B>> {
    return concatMap { res ->
        mapper(res).map { Pair(res, it) }
    }
}

inline fun <A, B, C> Flowable<Pair<A, B>>.flatMapChainResultPair(crossinline mapper: (Pair<A, B>) -> Flowable<C>): Flowable<Triple<A, B, C>> {
    return flatMap { res ->
        mapper(res).map { Triple(res.first, res.second, it) }
    }
}

data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val forth: D
)

inline fun <A, B, C, D> Single<Triple<A, B, C>>.flatMapChainResultTriple(crossinline mapper: (Triple<A, B, C>) -> Single<D>): Single<Quadruple<A, B, C, D>> {
    return flatMap { res ->
        mapper(res).map { Quadruple(res.first, res.second, res.third, it) }
    }
}
