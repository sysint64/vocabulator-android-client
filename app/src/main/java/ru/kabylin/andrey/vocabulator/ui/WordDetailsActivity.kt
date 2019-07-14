package ru.kabylin.andrey.vocabulator.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
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
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.ui.adapters.WordDetailsAdapter
import ru.kabylin.andrey.vocabulator.ui.models.WordDetailsItemVariant
import ru.kabylin.andrey.vocabulator.views.*

class WordDetailsActivity : ClientAppCompatActivity<ClientViewMediator>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = WordsRouter(this)
    override val client: Client by instance()
    override val viewMediator by lazy { ClientViewMediator(client, this, lifecycle) }

    private val wordsService: WordsService by instance()
    private val items = ArrayList<WordDetailsItemVariant>()

    private val recyclerAdapter by lazy {
        WordDetailsAdapter(this, items)
    }

    private val ref by lazy {
        intent.extras["ref"] as String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_details)

        toolbar.attachToActivity(this, displayHomeButton = true)
        errorsView.attach(container)

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        //

        wordStatusesContainer.hideView()
        revealAnswerButton.hideView()
        rightButton.hideView()
        wrongButton.hideView()
    }

    override fun viewStateRefresh() {
        super.viewStateRefresh()
        getDetails()
    }

    private fun getDetails() {
        val query = wordsService.getWordDetails(ref)

        wordTextView.text = ""
        items.clear()

        client.execute(query) {
            val details = it.payload
            wordTextView.text = details.name
            items.addAll(details.details)
            recyclerAdapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(0)
        }
    }

    override fun onRequestStateUpdated(requestState: ClientResponse<RequestState>) {
        when (requestState.payload) {
            RequestState.STARTED -> progressBar.showView()
            RequestState.FINISHED -> progressBar.hideView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_add_word, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_add_word -> {
                gotoScreen(WordsScreens.ADD_WORD)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
