package ru.kabylin.andrey.vocabulator

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import org.kodein.di.generic.kcontext
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.router.Router
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.client.ClientResponse
import ru.kabylin.andrey.vocabulator.client.RequestState
import ru.kabylin.andrey.vocabulator.ext.hideView
import ru.kabylin.andrey.vocabulator.ext.showView
import android.support.v7.widget.helper.ItemTouchHelper
import ru.kabylin.andrey.vocabulator.holders.CategoryCardHolder
import ru.kabylin.andrey.vocabulator.views.*

class MainActivity : ClientAppCompatActivity<ClientViewState>(), KodeinAware {
    override val kodeinContext = kcontext(this)
    override val kodein by closestKodein()

    override val router = Router(this)
    override val client: Client by instance()
    override val viewState by lazy { ClientViewState(client, this) }

    private val wordsService: WordsService by instance()

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

        items.add(
            WordsService.Category(
                ref = "",
                image = "https://images.unsplash.com/photo-1489065094455-c2d576ff27a0?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=eb24765f872afe8f0daf28dec236b745&w=1000&q=80",
                name = "Category 1"
            )
        )

        items.add(
            WordsService.Category(
                ref = "",
                image = null,
                name = "Category 2"
            )
        )

        items.add(
            WordsService.Category(
                ref = "",
                image = null,
                name = "Category 3"
            )
        )

        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        val callback = GridDragAndDropItemTouchHelperCallback(recyclerAdapter)
        val touchHelper = ItemTouchHelper(callback)

        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun onCategoryClick(category: WordsService.Category) {
    }

    override fun onRequestStateUpdated(requestState: ClientResponse<RequestState>) {
        when (requestState.payload) {
            RequestState.STARTED -> progressBar.showView()
            RequestState.FINISHED -> progressBar.hideView()
        }
    }
}
