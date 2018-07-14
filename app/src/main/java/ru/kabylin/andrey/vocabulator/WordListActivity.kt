package ru.kabylin.andrey.vocabulator

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_word_list.*
import kotlinx.android.synthetic.main.item_word_score_count.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.holders.WordInListHolder
import ru.kabylin.andrey.vocabulator.router.Router
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.*

class WordListActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = Router(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this) }

    private val wordsService: WordsService by instance()
    private val items = ArrayList<WordsService.Word>()

    private val recyclerAdapter by lazy {
        SingleSwipableItemRecyclerAdapter(this, items, R.layout.item_word,
            ::WordInListHolder)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        categoryTitleTextView.text = "Phrasal verbs"

        toolbar.attachToActivity(this)
        errorsView.attach(container)

        items.add(
            WordsService.Word(
                ref = "",
                name = "put out",
                score = 1
            )
        )

        items.add(
            WordsService.Word(
                ref = "",
                name = "take out",
                score = 3
            )
        )

        items.add(
            WordsService.Word(
                ref = "",
                name = "look forward",
                score = 8
            )
        )

        items.add(
            WordsService.Word(
                ref = "",
                name = "give up",
                score = 10
            )
        )

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //

        addScore("New", 0, 0)

        for (i in 1..10) {
            addScore(i.toString(), i, 10-i)
        }
    }

    private fun addScore(title: String, score: Int, count: Int) {
        val scores = layoutInflater.inflate(R.layout.item_word_score_count, scoresContainer, false) as ViewGroup
        scoresContainer.addView(scores)

        scores.scoreTextView.text = title
        scores.countTextView.text = count.toString()
        scores.countTextView.setTextColor(getScoreColor(0, 150, score))
    }
}
