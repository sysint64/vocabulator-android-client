package ru.kabylin.andrey.vocabulator.tools.socialmedia.backends

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import io.reactivex.subjects.SingleSubject
import ru.kabylin.andrey.vocabulator.tools.socialmedia.CancelReason
import ru.kabylin.andrey.vocabulator.tools.socialmedia.SocialMediaBackend
import ru.kabylin.andrey.vocabulator.tools.socialmedia.SocialMediaError

class SocialMediaVkBackend : SocialMediaBackend {
    private var subject = SingleSubject.create<String>()

    override fun login(context: Context): SingleSubject<String> {
        VKSdk.login(context as Activity)
        subject = SingleSubject.create<String>()
        return subject
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        val vkCallback = object : VKCallback<VKAccessToken> {
            override fun onResult(res: VKAccessToken) {
                subject.onSuccess(res.accessToken)
            }

            override fun onError(error: VKError) {
                subject.onError(SocialMediaError(error.errorMessage, error))
            }
        }

        val vkResult = VKSdk.onActivityResult(requestCode, resultCode, data, vkCallback)

        if (vkResult && resultCode == Activity.RESULT_CANCELED) {
            subject.onError(SocialMediaError(reason = CancelReason))
            return false
        }

        return vkResult
    }
}
