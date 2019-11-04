package ru.kabylin.andrey.vocabulator.ui

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_train_menu.*
import kotlinx.android.synthetic.main.activity_word_details.*
import kotlinx.android.synthetic.main.activity_word_details.container
import kotlinx.android.synthetic.main.activity_word_details.toolbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.R
import ru.kabylin.andrey.vocabulator.Settings
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.services.TrainService
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewMediator
import ru.kabylin.andrey.vocabulator.views.attachToActivity

class TrainMenuActivity : ClientAppCompatActivity<ClientViewMediator>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = AppRouter(this)
    override val client: Client by instance()
    override val viewMediator by lazy { ClientViewMediator(client, this, lifecycle) }

    private val trainService: TrainService by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train_menu)

        toolbar.attachToActivity(this, displayHomeButton = true)
        errorsView.attach(container)

        setupRadioButtons()
        buttonTrain.setOnClickListener { onTrainClick() }
    }

    private fun setupRadioButtons() {
        when (Settings.currentWordTitle) {
            "${WordsService.Title.WORD}" ->
                radioButtonTitleWord.isChecked = true

            "${WordsService.Title.TRANSLATION_OR_DEFINITION}" ->
                radioButtonTitleTranslation.isChecked = true

            "${WordsService.Title.TRANSCRIPTION_OR_WORD}" ->
                radioButtonTitleTranscription.isChecked = true

            else -> radioButtonTitleWord.isChecked = true
        }

        when (Settings.currentMode) {
            "${TrainService.Mode.LEARNING}" ->
                radioButtonModeLearning.isChecked = true

            "${TrainService.Mode.REVISION}" ->
                radioButtonModeRevision.isChecked = true

            "${TrainService.Mode.RANDOM}" ->
                radioButtonModeRandom.isChecked = true

            else -> radioButtonModeRevision.isChecked = true
        }
    }

    private fun onTrainClick() {
        val query = trainService.setWordTitle(getWordTitle())
            .andThen(trainService.setMode(getMode()))

        client.execute(query) {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun getMode(): TrainService.Mode =
        when {
            radioButtonModeLearning.isChecked ->
                TrainService.Mode.LEARNING

            radioButtonModeRevision.isChecked ->
                TrainService.Mode.REVISION

            radioButtonModeRandom.isChecked ->
                TrainService.Mode.RANDOM

            else -> TrainService.Mode.REVISION
        }

    private fun getWordTitle(): WordsService.Title =
        when {
            radioButtonTitleWord.isChecked ->
                WordsService.Title.WORD

            radioButtonTitleTranslation.isChecked ->
                WordsService.Title.TRANSLATION_OR_DEFINITION

            radioButtonTitleTranscription.isChecked ->
                WordsService.Title.TRANSCRIPTION_OR_WORD

            else -> WordsService.Title.WORD
        }
}
