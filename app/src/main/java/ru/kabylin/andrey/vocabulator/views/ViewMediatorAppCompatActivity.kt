package ru.kabylin.andrey.vocabulator.views

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable

abstract class ViewMediatorAppCompatActivity<out T : ViewMediator> : AppCompatActivity(), ViewMediatorAware {
    abstract override val viewMediator: T

    val lifecycleDisposer: CompositeDisposable
        get() = viewMediator.lifecycleDisposer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewMediator.enable()
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
