package ru.kabylin.andrey.vocabulator.compositors

import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.assertions.shouldHave
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.complete
import com.rubylichtenstein.rxtest.matchers.error
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.client.RequestState
import java.util.concurrent.TimeUnit

class RequestStateCompositorTest {
    private val client = Client()
    private val compositor = RequestStateCompositor(client)
    private val values = ArrayList<RequestState>()

    init {
        client.requestState.subscribe { values.add(it.payload) }
    }

    private fun checkRequestStates() {
        Assert.assertEquals(2, values.size)
        Assert.assertEquals(RequestState.STARTED, values[0])
        Assert.assertEquals(RequestState.FINISHED, values[1])
    }

    @Test
    fun `should update request state for Single`() {
        compositor.compose(Single.just(10))
            .test { it should complete() }
        checkRequestStates()
    }

    @Test
    fun `should update request state for Completable`() {
        compositor.compose(Completable.create { it.onComplete() })
            .test { it should complete() }
        checkRequestStates()
    }

    @Test
    fun `should update request state for Flowable`() {
        compositor.compose(Flowable.just(10, 11, 12))
            .test { it should complete() }
        checkRequestStates()
    }

    @Test
    fun `should update request state for Observable`() {
        compositor.compose(Observable.just(10, 11, 12))
            .test { it should complete() }
        checkRequestStates()
    }

// Errors ------------------------------------------------------------------------------------------

    @Test
    fun `should update request state for Single on errors`() {
        compositor.compose(Single.error<Int>(AssertionError()))
            .test { it shouldHave error(AssertionError::class.java) }
        checkRequestStates()
    }

    @Test
    fun `should update request state for Completable on errors`() {
        compositor.compose(Completable.error(AssertionError()))
            .test { it shouldHave error(AssertionError::class.java) }
        checkRequestStates()
    }

    @Test
    fun `should update request state for Flowable on errors`() {
        compositor.compose(Flowable.error<Int>(AssertionError()))
            .test { it shouldHave error(AssertionError::class.java) }
        checkRequestStates()
    }

    @Test
    fun `should update request state for Observable on errors`() {
        compositor.compose(Observable.error<Int>(AssertionError()))
            .test { it shouldHave error(AssertionError::class.java) }
        checkRequestStates()
    }

// Cancel ------------------------------------------------------------------------------------------

    @Test
    fun `should update request state for Single on cancel`() {
        compositor.compose(Single.just(1)
            .delay(10, TimeUnit.SECONDS))
            .test { it.cancel() }

        checkRequestStates()
    }

    @Test
    fun `should update request state for Completable cancel`() {
        compositor.compose(Completable.create { it.onComplete() }
            .delay(10, TimeUnit.SECONDS))
            .test { it.cancel() }

        checkRequestStates()
    }

    @Test
    fun `should update request state for Flowable cancel`() {
        compositor.compose(Flowable.just(10, 11, 12)
            .delay(10, TimeUnit.SECONDS))
            .test { it.cancel() }

        checkRequestStates()
    }

    @Test
    fun `should update request state for Observable cancel`() {
        compositor.compose(Observable.just(10, 11, 12)
            .delay(10, TimeUnit.SECONDS))
            .test { it.cancel() }

        checkRequestStates()
    }
}
