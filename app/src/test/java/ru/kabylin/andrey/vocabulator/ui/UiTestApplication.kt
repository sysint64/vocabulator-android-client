package ru.kabylin.andrey.vocabulator.ui

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.robolectric.TestLifecycleApplication
import ru.kabylin.andrey.vocabulator.R
import java.lang.reflect.Method

class UiTestApplication : Application(), KodeinAware, TestLifecycleApplication {
    override val kodein =
        Kodein.lazy {
            import(uiTestsDependencies(this@UiTestApplication), allowOverride = true)
        }

    override fun onCreate() {
        super.onCreate()

        Kotpref.init(this)
        Kotpref.gson = Gson()

        setTheme(R.style.AppTheme)
    }

    override fun beforeTest(method: Method?) {}

    override fun prepareTest(test: Any?) {
    }

    override fun afterTest(method: Method?) {
    }
}
