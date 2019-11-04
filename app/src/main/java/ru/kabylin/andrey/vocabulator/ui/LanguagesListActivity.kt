package ru.kabylin.andrey.vocabulator.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_languages.*
import kotlinx.android.synthetic.main.activity_languages.container
import kotlinx.android.synthetic.main.activity_languages.progressBar
import kotlinx.android.synthetic.main.activity_languages.recyclerView
import kotlinx.android.synthetic.main.activity_languages.toolbar
import kotlinx.android.synthetic.main.activity_word_details.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.R
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.client.ClientResponse
import ru.kabylin.andrey.vocabulator.client.RequestState
import ru.kabylin.andrey.vocabulator.ext.hideView
import ru.kabylin.andrey.vocabulator.ext.showView
import ru.kabylin.andrey.vocabulator.services.LanguagesService
import ru.kabylin.andrey.vocabulator.ui.holders.LanguageHolder
import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewMediator
import ru.kabylin.andrey.vocabulator.views.SingleSwipableItemRecyclerAdapter
import ru.kabylin.andrey.vocabulator.views.attachToActivity

class LanguagesListActivity : ClientAppCompatActivity<ClientViewMediator>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = AppRouter(this)
    override val client: Client by instance()
    override val viewMediator by lazy { ClientViewMediator(client, this, lifecycle) }

    private val languagesService: LanguagesService by instance()

    private val items = ArrayList<LanguagesService.Language>()

    private val recyclerAdapter by lazy {
        SingleSwipableItemRecyclerAdapter(this, items, R.layout.item_language,
            ::LanguageHolder, ::onLanguageClick)
    }

    companion object {
        const val REQUEST_CODE_GET_LANGUAGES = 2001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_languages)

        toolbar.attachToActivity(this, displayHomeButton = true)
        errorsView.attach(container)

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    override fun viewStateRefresh() {
        super.viewStateRefresh()
        getLanguages()
    }

    private fun getLanguages() {
        val query = languagesService.getLanguages()

        client.execute(query, REQUEST_CODE_GET_LANGUAGES) {
            items.clear()
            items.addAll(it.payload)
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun onLanguageClick(language: LanguagesService.Language) {
        val query = languagesService.selectLanguage(language.ref)

        client.execute(query) {
            finish()
        }
    }

    override fun onRequestStateUpdated(requestState: ClientResponse<RequestState>) {
        if (requestState.requestCode != REQUEST_CODE_GET_LANGUAGES) {
            return
        }

        when (requestState.payload) {
            RequestState.STARTED -> {
                progressBar.showView()
                recyclerView.hideView()
            }
            RequestState.FINISHED -> {
                progressBar.hideView()
                recyclerView.showView()
            }
        }
    }
}
