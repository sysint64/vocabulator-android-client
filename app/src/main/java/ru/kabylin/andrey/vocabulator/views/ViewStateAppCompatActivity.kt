package ru.kabylin.andrey.vocabulator.views

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

abstract class ViewStateAppCompatActivity<out T : ViewState> : AppCompatActivity(), ViewStateAware {
    abstract override val viewState: T

    override fun onResume() {
        super.onResume()
        viewState.subscribe()
        viewStateRefresh()
    }

    override fun onPause() {
        super.onPause()
        viewState.unsubscribe()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
