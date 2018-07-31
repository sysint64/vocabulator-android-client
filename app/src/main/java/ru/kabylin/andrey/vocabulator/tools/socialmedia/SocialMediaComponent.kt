package ru.kabylin.andrey.vocabulator.tools.socialmedia

import android.app.Activity
import android.content.Intent

class SocialMediaComponent {
    private val backends = HashMap<String, SocialMediaBackend>()

    fun addBackend(key: String, backend: SocialMediaBackend) {
        backends[key] = backend
    }

    fun getBackend(key: String): SocialMediaBackend {
        return backends[key]!!
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode != Activity.RESULT_OK)
            return false

        var res = false

        for (backend in backends.values)
            res = res || backend.onActivityResult(requestCode, resultCode, data)

        return res
    }
}
