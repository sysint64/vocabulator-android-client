package ru.kabylin.andrey.vocabulator.ui

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_add_word.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.R
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.ClientAppCompatActivity
import ru.kabylin.andrey.vocabulator.views.ClientViewMediator
import ru.kabylin.andrey.vocabulator.views.attachToActivity

class AddWordActivity : ClientAppCompatActivity<ClientViewMediator>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = AppRouter(this)
    override val client: Client by instance()
    override val viewMediator by lazy { ClientViewMediator(client, this, lifecycle) }

    private val wordsService: WordsService by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)

        toolbar.attachToActivity(this, displayHomeButton = true)
        errorsView.attach(container)

        addButton.setOnClickListener {
            val query = wordsService.addNewWord(
                WordsService.NewWord(
                    name = nameTextInput.text.toString(),
                    translation = translationTextInput.text.toString()
                )
            )

            client.execute(query) {
                finish()
            }
        }
    }
}
