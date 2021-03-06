package ru.kabylin.andrey.vocabulator.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_word_list.*
import kotlinx.android.synthetic.main.activity_word_list.container
import kotlinx.android.synthetic.main.activity_word_list.floatingActionButtonTrain
import kotlinx.android.synthetic.main.activity_word_list.recyclerView
import kotlinx.android.synthetic.main.activity_word_list.toolbar
import kotlinx.android.synthetic.main.item_word_score_count.view.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import ru.kabylin.andrey.vocabulator.R
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.ext.hideView
import ru.kabylin.andrey.vocabulator.getScoreColor
import ru.kabylin.andrey.vocabulator.ui.holders.WordInListHolder
import ru.kabylin.andrey.vocabulator.services.TrainService
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.*

class WordListActivity : ClientAppCompatActivity<ClientViewMediator>(), KodeinAware {
    override val kodein by closestKodein()

    override val router = AppRouter(this)
    override val client: Client by instance()
    override val viewMediator by lazy { ClientViewMediator(client, this, lifecycle) }

    private val wordsService: WordsService by instance()
    private val trainService: TrainService by instance()

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

        categoryTitleTextView.text = intent.extras["categoryName"] as String
        categoryTitleTextView.hideView()
        title = intent.extras["categoryName"] as String

        toolbar.attachToActivity(this, displayHomeButton = true)
        errorsView.attach(container)

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        floatingActionButtonTrain.setOnClickListener {
            onTrainClick()
        }

        //

        addScore("New", 0)

        for (i in 1..10)
            addScore(i.toString(), i)
    }

    private fun onTrainClick() {
        val query = trainService.setCategory(categoryRef)

        client.execute(query) {
            gotoScreen(Routes.TRAIN_MENU)
        }
    }

    override fun viewStateRefresh() {
        super.viewStateRefresh()
        getWords()
        getScoresCounts()
    }

    private fun getWords() {
        val query = wordsService.getWordsForCategory(
            categoryRef,
            title = WordsService.Title.WORD,
            orderBy = WordsService.OrderBy.SCORE
        )

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
        gotoScreen(Routes.DETAILS, mapOf("ref" to word.ref))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_add_word, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_add_word -> {
                gotoScreen(Routes.ADD_WORD)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Routes.RESULT_CODE_TRAIN_MENU && resultCode == Activity.RESULT_OK) {
            val query = trainService.startTraining()

            client.execute(query) {
                gotoScreen(Routes.TRAIN)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
