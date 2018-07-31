package ru.kabylin.andrey.vocabulator.tools.socialmedia

import android.content.Context
import android.content.Intent
import io.reactivex.subjects.SingleSubject

interface SocialMediaBackend {
    fun login(context: Context): SingleSubject<String>

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return false
    }
}
