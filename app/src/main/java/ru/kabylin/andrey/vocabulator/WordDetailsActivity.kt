package ru.kabylin.andrey.vocabulator

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_word_details.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.client.ClientResponse
import ru.kabylin.andrey.vocabulator.client.RequestState
import ru.kabylin.andrey.vocabulator.ext.hideView
import ru.kabylin.andrey.vocabulator.ext.showView
import ru.kabylin.andrey.vocabulator.ext.subscribeOnSuccess
import ru.kabylin.andrey.vocabulator.router.Router
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.*
import java.util.concurrent.TimeUnit

class WordDetailsActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = WordsRouter(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this) }

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

        client.execute(query) {
            val details = it.payload
            wordTextView.text = details.name

            for (item in details.details) {
                items.add(WordDetailsItemVariant(title = item.title))
                items.add(WordDetailsItemVariant(desc = item.value))
            }

            if (details.translations.isNotEmpty())
                items.add(WordDetailsItemVariant(title = "Translations"))

            for (translation in details.translations)
                items.add(WordDetailsItemVariant(listItem = translation))

            for (definition in details.definitions)
                items.add(WordDetailsItemVariant(definition = definition))

            // Android 5 bug :(
            Single.just(Unit)
                .subscribeOn(AndroidSchedulers.mainThread())
                .delaySubscription(500, TimeUnit.MILLISECONDS)
                .subscribeOnSuccess {
                    recyclerView.scrollToPosition(0)
                }
        }
    }

    override fun onRequestStateUpdated(requestState: ClientResponse<RequestState>) {
        when (requestState.payload) {
            RequestState.STARTED -> progressBar.showView()
            RequestState.FINISHED -> progressBar.hideView()
        }
    }
}
