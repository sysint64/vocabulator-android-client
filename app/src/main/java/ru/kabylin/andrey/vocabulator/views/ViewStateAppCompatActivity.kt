package ru.kabylin.andrey.vocabulator.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable

abstract class ViewStateAppCompatActivity<out T : ViewState> : AppCompatActivity(), ViewStateAware {
    abstract override val viewState: T

    val lifecycleDisposer: CompositeDisposable
        get() = viewState.lifecycleDisposer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewState.enable()
    }

    override fun onPostResume() {
        super.onPostResume()
        viewStateRefresh()
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
