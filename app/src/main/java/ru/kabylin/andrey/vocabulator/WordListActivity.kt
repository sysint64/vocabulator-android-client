package ru.kabylin.andrey.vocabulator

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_word_list.*
import kotlinx.android.synthetic.main.item_word_score_count.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.holders.WordInListHolder
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.*

class WordListActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = WordsRouter(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this) }

    private val wordsService: WordsService by instance()
    private val items = ArrayList<WordsService.Word>()
    private val scoresViews = ArrayList<Pair<Int, View>>()

    private val categoryRef by lazy {
        intent.extras["categoryRef"] as String
    }

    private val recyclerAdapter by lazy {
        SingleSwipableItemRecyclerAdapter(this, items, R.layout.item_word,
            ::WordInListHolder, ::onWordClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_list)

        categoryTitleTextView.text = "Phrasal verbs"

        toolbar.attachToActivity(this, displayHomeButton = true)
        errorsView.attach(container)

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //

        addScore("New", 0)

        for (i in 1..10)
            addScore(i.toString(), i)

        //

        wordTranslationModeButton.setOnClickListener {
            val extras = mapOf(
                "categoryRef" to categoryRef,
                "mode" to "word-translation"
            )
            gotoScreen(WordsScreens.TRAIN, extras)
        }

        translationWorldModeButton.setOnClickListener {
            val extras = mapOf(
                "categoryRef" to categoryRef,
                "mode" to "translation-word"
            )
            gotoScreen(WordsScreens.TRAIN, extras)
        }
    }

    override fun viewStateRefresh() {
        super.viewStateRefresh()
        getWords()
        getScoresCounts()
    }

    private fun getWords() {
        val query = wordsService.getWordsForCategory(categoryRef)

        client.execute(query) {
            items.clear()
            items.addAll(it.payload)
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun getScoresCounts() {
        val query = wordsService.getScoresCounts(categoryRef)

        client.execute(query) {
            for (categoryScore in it.payload) {
                val container = scoresViews.first { (score, _) -> score == categoryScore.score }.second
                container.countTextView.text = categoryScore.count.toString()
            }
        }
    }

    private fun addScore(title: String, score: Int) {
        val container = layoutInflater.inflate(R.layout.item_word_score_count, scoresContainer, false) as ViewGroup
        scoresContainer.addView(container)

        container.scoreTextView.text = title
        container.countTextView.text = "0"
        container.countTextView.setTextColor(getScoreColor(0, 150, score))

        scoresViews.add(Pair(score, container))
    }

    private fun onWordClick(word: WordsService.Word) {
        gotoScreen(WordsScreens.DETAILS, mapOf("ref" to word.ref))
    }
}
