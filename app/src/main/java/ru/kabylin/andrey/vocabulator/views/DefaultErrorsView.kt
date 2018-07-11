package ru.kabylin.andrey.vocabulator.views

import android.content.Context
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import org.jetbrains.anko.find
import ru.kabylin.andrey.vocabulator.R
import ru.kabylin.andrey.vocabulator.client.*
import ru.kabylin.andrey.vocabulator.ext.hideView
import ru.kabylin.andrey.vocabulator.ext.showView

open class DefaultErrorsView(val context: Context) {
    var bottomBoardErrorView: ConstraintLayout? = null
        private set

    var bottomBoardErrorTitleTextView: TextView? = null
        private set

    var notFoundErrorView: View? = null
        private set

    var throwable: ClientResponse<Throwable?> = ClientResponse(null, success = true)

    fun attach(container: ViewGroup) {
        attachBottomBoardErrorView(container)
        attachNotFoundErrorView(container)
    }

    private fun attachBottomBoardErrorView(container: ViewGroup) {
        bottomBoardErrorView = inflateFullScreenLayout(context, container, R.layout.part_error_board) as ConstraintLayout

        bottomBoardErrorTitleTextView = bottomBoardErrorView?.find(R.id.titleTextView)
        val closeButton = bottomBoardErrorView?.find<View>(R.id.closeButton)

        closeButton?.setOnClickListener {
            bottomBoardErrorView?.hideView()

            if (context is ErrorViewAware)
                context.onErrorViewDismissErrorClick(throwable)
        }
    }

    private fun attachNotFoundErrorView(container: ViewGroup) {
        notFoundErrorView = inflateFullScreenLayout(context, container, R.layout.part_error_not_found)
        notFoundErrorView?.find<Button>(R.id.closeButton)?.setOnClickListener {
            if (context is ErrorViewAware) {
                context.onErrorViewCloseClick()
            } else {
                hideAll()
            }
        }
    }

    fun setError(error: ClientResponse<Throwable?>) {
        if (throwable == error)
            return

        if (error.payload == null) {
            hideAll()
            return
        }

        if (context is ErrorViewAware) {
            // Управление на отображение ошибки перехвачено
            if (context.displayError(error))
                return
        }

        hideAll()
        throwable = error

        when (error.payload) {
            is AccessError -> when (error.payload.reason) {
                AccessErrorReason.LOST_CONNECTION -> showLostConnectionError()
                AccessErrorReason.TIMEOUT -> showTimeOutError()
                AccessErrorReason.NOT_FOUND -> showNotFoundError()
                AccessErrorReason.TOO_MANY_REQUESTS -> showTooManyRequestsError()
                AccessErrorReason.BAD_RESPONSE -> showBadResponseError()
                AccessErrorReason.INTERNAL_SERVER_ERROR -> showInternalServerError()
                is AccessErrorReason.VERSION_ERROR -> showVersionError(error.payload.reason.level)
                is AccessErrorReason.UNSPECIFIED -> showUnspecifiedAccessError(error.payload.reason)
            }
            is LogicError -> {
                showLogicError(error.payload.reason)
            }
            is CredentialsError -> {
                showCredentialsError(error.payload.message!!)
            }
        }
    }

    private fun showLogicError(reason: Reason) {
        showBottomBoard(reason.toString(context))
    }

    private fun showCredentialsError(message: String) {
        showBottomBoard(message)
    }

    private fun hideAll() {
        bottomBoardErrorView?.hideView()
        notFoundErrorView?.hideView()
    }

    private fun showLostConnectionError() {
        showBottomBoard(R.string.lost_connection)
    }

    private fun showTimeOutError() {
        showBottomBoard(R.string.time_is_out)
    }

    private fun showNotFoundError() {
        notFoundErrorView?.showView()
    }

    private fun showTooManyRequestsError() {
        showBottomBoard(R.string.too_many_requests)
    }

    private fun showBadResponseError() {
        showBottomBoard(R.string.bad_response)
    }

    private fun showInternalServerError() {
        showBottomBoard(R.string.internal_server_error)
    }

    private fun showVersionError(level: VersionErrorLevel) {
        if (level == VersionErrorLevel.WARNING) {
        }
    }

    private fun showUnspecifiedAccessError(reason: AccessErrorReason.UNSPECIFIED) {
        showBottomBoard(reason.message)
    }

    private fun showBottomBoard(@StringRes text: Int) {
        showBottomBoard(context.getString(text))
    }

    private fun showBottomBoard(text: String?) {
        bottomBoardErrorView?.showView()
        bottomBoardErrorTitleTextView?.textSize = 14f
        bottomBoardErrorTitleTextView?.text = text ?: context.getText(R.string.unspecified_access_error)
    }
}
