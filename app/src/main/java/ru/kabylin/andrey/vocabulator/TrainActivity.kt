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

class TrainActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
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

    enum class WordStatus {
        AWAIT,
        CORRECT,
        INCORRECT,
    }

    enum class State {
        ANSWER,
        REVEALED
    }

    private val wordStatuses = ArrayList<WordStatus>()
    private val wordStatusViews = ArrayList<ImageView>()

    private var screenState = State.ANSWER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_details)

        toolbar.attachToActivity(this, displayHomeButton = true)
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

        //

        for (i in 0..10) {
            wordStatuses.add(WordStatus.AWAIT)
            val imageView = layoutInflater.inflate(R.layout.item_word_status, wordStatusesContainer, false) as ImageView

            imageView.setImageResource(R.drawable.ic_checkbox_blank_circle_outline)
            wordStatusesContainer.addView(imageView)
            wordStatusViews.add(imageView)
        }

        updateState()

        revealAnswerButton.setOnClickListener {
            screenState = State.REVEALED
            updateState()
        }

        rightButton.setOnClickListener {
            screenState = State.ANSWER
            updateState()
        }

        wrongButton.setOnClickListener {
            screenState = State.ANSWER
            updateState()
        }
    }

    private fun updateState() {
        when (screenState) {
            State.ANSWER -> {
                revealAnswerButton.showView()
                rightButton.hideView()
                wrongButton.hideView()
                recyclerView.hideView()
            }
            State.REVEALED -> {
                revealAnswerButton.hideView()
                rightButton.showView()
                wrongButton.showView()
                recyclerView.showView()
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
