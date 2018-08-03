package ru.kabylin.andrey.vocabulator

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.client.ClientResponse
import ru.kabylin.andrey.vocabulator.client.RequestState
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem
import ru.kabylin.andrey.vocabulator.ext.*
import ru.kabylin.andrey.vocabulator.holders.CategoryCardHolder
import ru.kabylin.andrey.vocabulator.services.SyncService
import ru.kabylin.andrey.vocabulator.tools.isNetworkAvailable
import ru.kabylin.andrey.vocabulator.views.*
import java.util.*

class MainActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = WordsRouter(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this, lifecycle) }

    private val wordsService: WordsService by instance()
    private val syncService: SyncService by instance()

    private val items = ArrayList<WordsService.Category>()

    private val recyclerAdapter by lazy {
        SingleSwipableItemRecyclerAdapter(this, items, R.layout.item_category_card,
            ::CategoryCardHolder, ::onCategoryClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.attachToActivity(this)
        errorsView.attach(container)

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        val callback = GridDragAndDropItemTouchHelperCallback(recyclerAdapter)
        val touchHelper = ItemTouchHelper(callback)

        touchHelper.attachToRecyclerView(recyclerView)
    }

    override fun viewStateRefresh() {
        super.viewStateRefresh()

        if (isNetworkAvailable(this) && Settings.nextSync < now().time) {
            sync()
        }
        else {
            getCategories()
        }
    }

    private fun getCategories() {
        val query = wordsService.getCategories()

        client.execute(query) {
            items.clear()
            items.addAll(it.payload)
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun onCategoryClick(category: WordsService.Category) {
        val extras = mapOf(
            "categoryRef" to category.ref,
            "categoryName" to category.name
        )
        gotoScreen(WordsScreens.LIST, extras)
    }

    override fun onRequestStateUpdated(requestState: ClientResponse<RequestState>) {
        when (requestState.payload) {
            RequestState.STARTED -> {
                progressBar.showView()
                recyclerView.hideView()
            }
            RequestState.FINISHED -> {
                progressBar.hideView()
                recyclerView.showView()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_sync, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_sync -> {
                sync()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun sync() {
        val query = syncService.sync()

        client.execute(query) {
            viewStateRefresh()
        }
    }

    override fun onRetryClick() {
        sync()
    }
}
