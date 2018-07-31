package ru.kabylin.andrey.vocabulator.tools.socialmedia.backends

import android.content.Context
import android.content.Intent
import io.reactivex.subjects.SingleSubject
import ru.kabylin.andrey.vocabulator.tools.socialmedia.SocialMediaBackend

class SocialMediaGoogleBackend : SocialMediaBackend {
    override fun login(context: Context): SingleSubject<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}