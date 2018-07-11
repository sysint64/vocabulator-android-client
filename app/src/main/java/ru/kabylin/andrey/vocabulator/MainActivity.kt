package ru.kabylin.andrey.vocabulator

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.router.Router
import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewState
import ru.kabylin.andrey.vocabulator.views.attachToActivity

class MainActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = Router(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.attachToActivity(this)
        errorsView.attach(container)
    }
}
