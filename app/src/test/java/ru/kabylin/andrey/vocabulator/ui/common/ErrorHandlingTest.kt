package ru.kabylin.andrey.vocabulator.ui.common

import android.content.Intent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.kabylin.andrey.vocabulator.ui.MainActivity
import ru.kabylin.andrey.vocabulator.ui.TrainActivity
import ru.kabylin.andrey.vocabulator.ui.WordDetailsActivity
import ru.kabylin.andrey.vocabulator.ui.WordListActivity
import ru.kabylin.andrey.vocabulator.ext.createBundle
import ru.kabylin.andrey.vocabulator.ui.BaseUiTest
import ru.kabylin.andrey.vocabulator.ui.UiTestApplication
import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewMediator

/**
 * Тест проверяет, что обработка ошибок на конкретных экранах соответствует ожиданиям.
 */
@Config(application = UiTestApplication::class)
@RunWith(RobolectricTestRunner::class)
class ErrorHandlingTest : BaseUiTest() {

    /**
     *  Метод [doTest] получет на вход класс экрана [cls], а также объект [errorResponder]
     *  представляющий ожидаемое поведение на конкретные ошибки и проверяет, соответствует ли
     *  реальность ожиданиям.
     */
    private fun doTest(
            cls: Class<out ClientAppCompatActivity<ClientViewMediator>>,
            extras: Map<String, Any>? = null,
            errorResponder: ErrorResponder = DefaultErrorsResponder()
    ) {
        val intent = Intent(application.applicationContext, cls)

        if (extras != null)
            intent.putExtras(createBundle(extras))

        val activity = Robolectric.buildActivity(cls, intent).setup().get()
        checkErrors(activity, errorResponder)
    }

    private fun checkErrors(
            activity: ClientAppCompatActivity<ClientViewMediator>,
            errorResponder: ErrorResponder = DefaultErrorsResponder()
    ) {
        // Views
        errorResponder.onVersionWarning(activity)
        errorResponder.onTooManyRequestError(activity)
        errorResponder.onTimeoutError(activity)
        errorResponder.onConnectionLostError(activity)
        errorResponder.onNotFound(activity)
        errorResponder.onBadResponseError(activity)
        errorResponder.onInternalServerError(activity)

        // Routes
        errorResponder.onCriticalError(activity)
        errorResponder.onVersionError(activity)
    }

    @Test
    fun testMainActivity() =
        doTest(MainActivity::class.java)

    @Test
    fun testWordListActivity() =
        doTest(WordListActivity::class.java, mapOf("categoryName" to "", "categoryRef" to ""))

    @Test
    fun testWordDetailsActivity() =
        doTest(WordDetailsActivity::class.java, mapOf("ref" to ""))

    @Test
    fun testTrainActivity() =
        doTest(TrainActivity::class.java)
}
