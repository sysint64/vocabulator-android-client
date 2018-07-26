package ru.kabylin.andrey.vocabulator.compositors

import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.assertions.shouldHave
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.complete
import com.rubylichtenstein.rxtest.matchers.error
import io.reactivex.*
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.TimeUnit

class RetryWithDelayCompositorTest {
    private val compositor = RetryWithDelayCompositor(
        maxRetries = 1,
        retryDelay = 100,
        timeUnit = TimeUnit.MILLISECONDS
    )

    private val compositorWithPredicate = RetryWithDelayCompositor(
        maxRetries = 1,
        retryDelay = 100,
        timeUnit = TimeUnit.MILLISECONDS,
        predicate = { it !is RuntimeException }
    )

    @Test
    fun `should retry on error with 100ms delay for single`() {
        var result = -1

        compositor.compose(createSingle())
            .subscribeBy { result = it }

        Thread.sleep(200)
        Assert.assertEquals(10, result)
    }

    @Test
    fun `should retry on error with 100ms delay for flowable`() {
        var result = -1

        compositor.compose(createFlowable())
            .subscribeBy { result = it }

        Thread.sleep(200)
        Assert.assertEquals(10, result)
    }

    @Test
    fun `should retry on error with 100ms delay for observble`() {
        var result = -1

        compositor.compose(createObservable())
            .subscribeBy { result = it }

        Thread.sleep(200)
        Assert.assertEquals(10, result)
    }

    @Test
    fun `should retry on error with 100ms delay for completable`() {
        var completed = false

        compositor.compose(createCompletable())
            .subscribeBy { completed = true }

        Thread.sleep(200)
        Assert.assertTrue(completed)
    }

    @Test
    fun `should not retry on error with 100ms delay for single with predicate`() {
        var result = -1
        var error = false

        compositorWithPredicate.compose(createSingle())
            .subscribeBy(onSuccess = { result = it }, onError = { error = true })

        Thread.sleep(200)
        Assert.assertEquals(-1, result)
        Assert.assertTrue(error)
    }

    @Test
    fun `should not retry on error with 100ms delay for flowable with predicate`() {
        var result = -1
        var error = false

        compositorWithPredicate.compose(createFlowable())
            .subscribeBy(onNext = { result = it }, onError = { error = true })

        Thread.sleep(200)
        Assert.assertEquals(-1, result)
        Assert.assertTrue(error)
    }

    @Test
    fun `should not retry on error with 100ms delay for observable with predicate`() {
        var result = -1
        var error = false

        compositorWithPredicate.compose(createObservable())
            .subscribeBy(onNext = { result = it }, onError = { error = true })

        Thread.sleep(200)
        Assert.assertEquals(-1, result)
        Assert.assertTrue(error)
    }

    @Test
    fun `should not retry on error with 100ms delay for completable with predicate`() {
        var completed = false
        var error = false

        compositorWithPredicate.compose(createCompletable())
            .subscribeBy(onComplete = { completed = true }, onError = { error = true })

        Thread.sleep(200)
        Assert.assertFalse(completed)
        Assert.assertTrue(error)
    }

// Helpers -----------------------------------------------------------------------------------------

    private var errorEmitted = false

    private fun handleEmitter(emitter: Emitter<Int>) {
        if (!errorEmitted) {
            emitter.onError(RuntimeException())
            errorEmitted = true
        } else {
            emitter.onNext(10)
        }
    }

    private fun handleSingleEmitter(emitter: SingleEmitter<Int>) {
        if (!errorEmitted) {
            emitter.onError(RuntimeException())
            errorEmitted = true
        } else {
            emitter.onSuccess(10)
        }
    }

    private fun handleCompletableEmitter(emitter: CompletableEmitter) {
        if (!errorEmitted) {
            emitter.onError(RuntimeException())
            errorEmitted = true
        } else {
            emitter.onComplete()
        }
    }

    private fun createSingle(): Single<Int> {
        val single = Single.create<Int> { handleSingleEmitter(it) }

        single.test { it shouldHave error(RuntimeException::class.java) }
        single.test { it shouldEmit 10 }

        errorEmitted = false
        return single
    }

    private fun createFlowable(): Flowable<Int> {
        val flowable = Flowable.create<Int>({ handleEmitter(it) }, BackpressureStrategy.LATEST)

        flowable.test { it shouldHave error(RuntimeException::class.java) }
        flowable.test { it shouldEmit 10 }

        errorEmitted = false
        return flowable
    }

    private fun createObservable(): Observable<Int> {
        val observable = Observable.create<Int> { handleEmitter(it) }

        observable.test { it shouldHave error(RuntimeException::class.java) }
        observable.test { it shouldEmit 10 }

        errorEmitted = false
        return observable
    }

    private fun createCompletable(): Completable {
        val completable = Completable.create { handleCompletableEmitter(it) }

        completable.test { it shouldHave error(RuntimeException::class.java) }
        completable.test { it should complete() }

        errorEmitted = false
        return completable
    }
}
