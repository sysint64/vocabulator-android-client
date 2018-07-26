package ru.kabylin.andrey.vocabulator.compositors

import com.rubylichtenstein.rxtest.assertions.shouldHave
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.error
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert
import org.junit.Test
import ru.kabylin.andrey.vocabulator.client.*
import java.net.ConnectException

class ErrorsCompositorTest {
    private val client = Client()
    private val compositor = ErrorsCompositor(client)

    private val criticalErrors = ArrayList<Throwable>()
    private val accessErrors = ArrayList<AccessError>()
    private val logicErrors = ArrayList<LogicError>()
    private val credentialsErrors = ArrayList<CredentialsError>()
    private val validationErrors = ArrayList<ValidationErrors>()

    init {
        client.criticalErrors.subscribe { criticalErrors.add(it.payload) }
        client.accessErrors.subscribe { accessErrors.add(it.payload) }
        client.logicErrors.subscribe { logicErrors.add(it.payload) }
        client.credentialsError.subscribe { credentialsErrors.add(it.payload) }
        client.validationErrors.subscribe { validationErrors.add(it.payload) }
    }

    private fun throwErrorForSingle(error: Throwable) {
        compositor.compose(Single.error<Int>(error))
            .test { it shouldHave error(error.javaClass) }
    }

    private fun throwErrorForFlowable(error: Throwable) {
        compositor.compose(Flowable.error<Int>(error))
            .test { it shouldHave error(error.javaClass) }
    }

    private fun throwErrorForObservable(error: Throwable) {
        compositor.compose(Observable.error<Int>(error))
            .test { it shouldHave error(error.javaClass) }
    }

    private fun throwErrorForCompletable(error: Throwable) {
        compositor.compose(Completable.error(error))
            .test { it shouldHave error(error.javaClass) }
    }

// Critical errors ---------------------------------------------------------------------------------

    @Test
    fun `should catch critical error for Single`() {
        throwErrorForSingle(RuntimeException())
        checkCriticalErrors()
    }

    @Test
    fun `should catch critical error for Completable`() {
        throwErrorForCompletable(RuntimeException())
        checkCriticalErrors()
    }

    @Test
    fun `should catch critical error for Flowable`() {
        throwErrorForFlowable(RuntimeException())
        checkCriticalErrors()
    }

    @Test
    fun `should catch critical error for Observable`() {
        throwErrorForObservable(RuntimeException())
        checkCriticalErrors()
    }

    private fun checkCriticalErrors() {
        Assert.assertEquals(1, criticalErrors.size)
        Assert.assertEquals(0, accessErrors.size)
        Assert.assertEquals(0, logicErrors.size)
        Assert.assertEquals(0, credentialsErrors.size)
        Assert.assertEquals(0, validationErrors.size)
    }

// Logic errors ------------------------------------------------------------------------------------

    @Test
    fun `should catch logic error for Single`() {
        throwErrorForSingle(LogicError("Some debug details"))
        throwErrorForSingle(LogicError("Should be 13"))
        checkLogicErrors()
    }

    @Test
    fun `should catch logic error for Completable`() {
        throwErrorForCompletable(LogicError("Some debug details"))
        throwErrorForCompletable(LogicError("Should be 13"))
        checkLogicErrors()
    }

    @Test
    fun `should catch logic error for Flowable`() {
        throwErrorForFlowable(LogicError("Some debug details"))
        throwErrorForFlowable(LogicError("Should be 13"))
        checkLogicErrors()
    }

    @Test
    fun `should catch logic error for Observable`() {
        throwErrorForObservable(LogicError("Some debug details"))
        throwErrorForObservable(LogicError("Should be 13"))
        checkLogicErrors()
    }

    private fun checkLogicErrors() {
        Assert.assertEquals(0, criticalErrors.size)
        Assert.assertEquals(0, accessErrors.size)
        Assert.assertEquals(2, logicErrors.size)
        Assert.assertEquals(0, credentialsErrors.size)
        Assert.assertEquals(0, validationErrors.size)

//        Assert.assertEquals("Some debug details", logicErrors[0].reason.toString())
//        Assert.assertEquals("Should be 13", logicErrors[1].reason.toString())
    }

// Access errors -----------------------------------------------------------------------------------

    @Test
    fun `should catch access error for Single`() {
        throwErrorForSingle(AccessError("Connection error", ConnectException()))
        checkAccessErrors()
    }

    @Test
    fun `should catch access error for Completable`() {
        throwErrorForCompletable(AccessError("Connection error", ConnectException()))
        checkAccessErrors()
    }

    @Test
    fun `should catch access error for Flowable`() {
        throwErrorForFlowable(AccessError("Connection error", ConnectException()))
        checkAccessErrors()
    }

    @Test
    fun `should catch access error for Observable`() {
        throwErrorForObservable(AccessError("Connection error", ConnectException()))
        checkAccessErrors()
    }

    private fun checkAccessErrors() {
        Assert.assertEquals(0, criticalErrors.size)
        Assert.assertEquals(1, accessErrors.size)
        Assert.assertEquals(0, logicErrors.size)
        Assert.assertEquals(0, credentialsErrors.size)
        Assert.assertEquals(0, validationErrors.size)

        Assert.assertTrue(accessErrors[0].cause is ConnectException)
        Assert.assertEquals("Connection error", accessErrors[0].message)
    }

// Credentials errors ------------------------------------------------------------------------------

    @Test
    fun `should catch credentials error for Single`() {
        throwErrorForSingle(CredentialsError("Not allowed"))
        checkCredentialsErrors()
    }

    @Test
    fun `should catch credentials error for Completable`() {
        throwErrorForCompletable(CredentialsError("Not allowed"))
        checkCredentialsErrors()
    }

    @Test
    fun `should catch credentials error for Flowable`() {
        throwErrorForFlowable(CredentialsError("Not allowed"))
        checkCredentialsErrors()
    }

    @Test
    fun `should catch credentials error for Observable`() {
        throwErrorForObservable(CredentialsError("Not allowed"))
        checkCredentialsErrors()
    }

    private fun checkCredentialsErrors() {
        Assert.assertEquals(0, criticalErrors.size)
        Assert.assertEquals(0, accessErrors.size)
        Assert.assertEquals(0, logicErrors.size)
        Assert.assertEquals(1, credentialsErrors.size)
        Assert.assertEquals(0, validationErrors.size)

        Assert.assertEquals("Not allowed", credentialsErrors[0].message)
    }

// Validation errors -------------------------------------------------------------------------------

    private fun createValidationsErrors() : Map<String, String> =
        mapOf(
            "phone" to "Non valid phone number",
            "name" to "Special chars is not allowed"
        )

    @Test
    fun `should catch validations error for Single`() {
        throwErrorForSingle(ValidationErrors(createValidationsErrors()))
        checkValidationsErrors()
    }

    @Test
    fun `should catch validations error for Completable`() {
        throwErrorForCompletable(ValidationErrors(createValidationsErrors()))
        checkValidationsErrors()
    }

    @Test
    fun `should catch validations error for Flowable`() {
        throwErrorForFlowable(ValidationErrors(createValidationsErrors()))
        checkValidationsErrors()
    }

    @Test
    fun `should catch validations error for Observable`() {
        throwErrorForObservable(ValidationErrors(createValidationsErrors()))
        checkValidationsErrors()
    }

    private fun checkValidationsErrors() {
        Assert.assertEquals(0, criticalErrors.size)
        Assert.assertEquals(0, accessErrors.size)
        Assert.assertEquals(0, logicErrors.size)
        Assert.assertEquals(0, credentialsErrors.size)
        Assert.assertEquals(1, validationErrors.size)

        Assert.assertEquals(2, validationErrors[0].errors.size)
        Assert.assertEquals("Non valid phone number", validationErrors[0].errors["phone"])
        Assert.assertEquals("Special chars is not allowed", validationErrors[0].errors["name"])
    }
}
