package ru.kabylin.andrey.vocabulator

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_word.*
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

class WordsActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = Router(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this) }

    private val wordsService: WordsService by instance()
    private val items = ArrayList<WordDetailsItemVariant>()

    private val recyclerAdapter by lazy {
        WordDetailsAdapter(this, items)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word)

        toolbar.attachToActivity(this)
        errorsView.attach(container)

        wordTextView.text = "breakthrough"

        items.add(WordDetailsItemVariant(title = "Pronounce"))
        items.add(WordDetailsItemVariant(desc = "ˈbrākˌTHro͞o"))

        items.add(WordDetailsItemVariant(title = "Translations"))
        items.add(WordDetailsItemVariant(listItem = "прорвать"))
        items.add(WordDetailsItemVariant(listItem = "прорыв"))
        items.add(WordDetailsItemVariant(listItem = "достижение"))

        val definition = WordsService.Definition(
            title = "noun",
            desc = "a sudden, dramatic, and important discovery or development.",
            example = "a major breakthrough in DNA research",
            synonyms = "advance, development, step forward, success, improvement, discovery, innovation, revolution, progress, headway".split(",")
        )

        items.add(WordDetailsItemVariant(definition = definition))

        val definition2 = WordsService.Definition(
            title = "noun",
            desc = "a sudden, dramatic, and important discovery or development.",
            example = "",
            synonyms = "advance, development".split(",")
        )

        items.add(WordDetailsItemVariant(definition = definition2))

        val definition3 = WordsService.Definition(
            title = "noun",
            desc = "a sudden, dramatic, and important discovery or development.",
            example = "a major breakthrough in DNA research",
            synonyms = listOf()
        )

        items.add(WordDetailsItemVariant(definition = definition3))

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Android 5 bug :(
        Single.just(Unit)
            .subscribeOn(AndroidSchedulers.mainThread())
            .delaySubscription(500, TimeUnit.MILLISECONDS)
            .subscribeOnSuccess {
                recyclerView.scrollToPosition(0)
            }
    }

    override fun onRequestStateUpdated(requestState: ClientResponse<RequestState>) {
        when (requestState.payload) {
            RequestState.STARTED -> progressBar.showView()
            RequestState.FINISHED -> progressBar.hideView()
        }
    }
}
