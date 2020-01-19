package ru.kabylin.andrey.vocabulator.ui

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import ru.kabylin.andrey.vocabulator.R
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.ext.flatMapChainResult
import ru.kabylin.andrey.vocabulator.services.SettingsService
import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewMediator
import ru.kabylin.andrey.vocabulator.views.attachToActivity

class SettingsActivity : ClientAppCompatActivity<ClientViewMediator>(), KodeinAware {
    override val kodein by closestKodein()
    override val router = AppRouter(this)
    override val client: Client by instance()
    override val viewMediator by lazy { ClientViewMediator(client, this, lifecycle) }

    private val settingsService: SettingsService by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar.attachToActivity(this)

        buttonSave.setOnClickListener {
            saveSettings()
        }
    }

    override fun viewStateRefresh() {
        super.viewStateRefresh()
        getSettings()
    }

    private fun getSettings() {
        val query = settingsService.getServerUrl()
            .flatMapChainResult { settingsService.getServerPort() }

        client.execute(query) {
            val (url, port) = it.payload

            editTextServerUrl.setText(url)
            editTextServerPort.setText(port.toString())
        }
    }

    private fun saveSettings() {
        val query = settingsService.setServerUrl(editTextServerUrl.text.toString())
            .andThen(settingsService.setServerPort(editTextServerPort.text.toString().toInt()))

        client.execute(query) {
            finish()
        }
    }
}
