package ru.kabylin.andrey.vocabulator

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.Gson
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.compositors.Compositor

class MainApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(dependencies(this@MainApplication))
    }


    override fun onCreate() {
        super.onCreate()

        Kotpref.init(this)
        Kotpref.gson = Gson()

        val client: Client by instance()
        val clientCompositor: Compositor by instance("client")
        client.compositor = clientCompositor
    }
}
