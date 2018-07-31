package ru.kabylin.andrey.vocabulator.tools.socialmedia.backends

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import io.reactivex.subjects.SingleSubject
import ru.kabylin.andrey.vocabulator.tools.socialmedia.CancelReason
import ru.kabylin.andrey.vocabulator.tools.socialmedia.SocialMediaBackend
import ru.kabylin.andrey.vocabulator.tools.socialmedia.SocialMediaError

class SocialMediaFbBackend : SocialMediaBackend {
    private val subject = SingleSubject.create<String>()
    private val callbackManager = CallbackManager.Factory.create()

    override fun login(context: Context): SingleSubject<String> {
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    subject.onSuccess(loginResult.accessToken.token)
                }

                override fun onCancel() {
                    subject.onError(SocialMediaError(reason = CancelReason))
                }

                override fun onError(exception: FacebookException) {
                    subject.onError(SocialMediaError(reason = exception))
                }
            })

        LoginManager.getInstance().logInWithReadPermissions(context as Activity, listOf("public_profile"))
        return subject
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
