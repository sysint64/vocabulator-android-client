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
import ru.kabylin.andrey.vocabulator.services.TrainService
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.*
import java.util.concurrent.TimeUnit
import android.view.animation.AnimationUtils
import ru.kabylin.andrey.vocabulator.ext.disposeBy
import ru.kabylin.andrey.vocabulator.views.anim.BounceInterpolator

class TrainActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = WordsRouter(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this, lifecycle) }

    private val trainService: TrainService by instance()

    private val items = ArrayList<WordDetailsItemVariant>()

    private val bounceAnim by lazy {
        val anim = AnimationUtils.loadAnimation(this, R.anim.bounce)
        val interpolator = BounceInterpolator(0.2, 20.0)
        anim.interpolator = interpolator
        anim
    }

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

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        createWordStatuses()
        initButtons()
        updateState()
        updateCurrentWordView()

        // Events
        trainService.newPageEvents()
            .subscribe { newPage() }
            .disposeBy(lifecycleDisposer)

        trainService.finishEvents()
            .subscribe { finish() }
            .disposeBy(lifecycleDisposer)
    }

    private fun initButtons() {
        revealAnswerButton.setOnClickListener {
            val query = trainService.reveal()
            client.execute(query) {
                screenState = State.REVEALED
                updateDetails(it.payload)
                updateState()
            }
        }

        rightButton.setOnClickListener {
            val query = trainService.right()
            client.execute(query) {
                screenState = State.ANSWER
                updateWordStatus(it.payload)
                updateState()
                nextWord()
            }
        }

        wrongButton.setOnClickListener {
            val query = trainService.wrong()
            client.execute(query) {
                screenState = State.ANSWER
                updateWordStatus(it.payload)
                updateState()
                nextWord()
            }
        }
    }

    private fun createWordStatuses() {
        for (i in 0..10) {
            wordStatuses.add(WordStatus.AWAIT)
            val imageView = layoutInflater.inflate(R.layout.item_word_status, wordStatusesContainer, false) as ImageView

            imageView.setImageResource(R.drawable.ic_checkbox_blank_circle_outline)
            wordStatusesContainer.addView(imageView)
            wordStatusViews.add(imageView)
        }
    }

    private fun newPage() {
        for (imageView in wordStatusViews) {
            imageView.setImageResource(R.drawable.ic_checkbox_blank_circle_outline)
            imageView.clearAnimation()
        }
    }

    private fun updateWordStatus(wordStatus: TrainService.WordStatus) {
        val imageView = wordStatusViews[wordStatus.pos]

        if (wordStatus.isRight) {
            imageView.setImageResource(R.drawable.ic_check_circle)
        } else {
            imageView.setImageResource(R.drawable.ic_close_circle)
        }

        imageView.startAnimation(bounceAnim)
    }

    private fun updateCurrentWordView() {
        val query = trainService.currentWord()
        client.execute(query) {
            wordTextView.text = it.payload.name
        }
    }

    private fun nextWord() {
        val query = trainService.nextWord()
        client.execute(query) {
            wordTextView.text = it.payload.name
        }
    }

    private fun updateDetails(details: WordsService.WordDetails) {
        items.clear()

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

        recyclerAdapter.notifyDataSetChanged()

        // Android 5 bug :(
        Single.just(Unit)
            .subscribeOn(AndroidSchedulers.mainThread())
            .delaySubscription(500, TimeUnit.MILLISECONDS)
            .subscribeOnSuccess {
                recyclerView.scrollToPosition(0)
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
