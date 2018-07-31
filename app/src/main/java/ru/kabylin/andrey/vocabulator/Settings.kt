package ru.kabylin.andrey.vocabulator

import com.chibatching.kotpref.KotprefModel
import java.util.*

object Settings : KotprefModel() {
    private var deviceId: String by stringPref(default = "")
    var nextSync: Long by longPref(default = 0L)

    fun retrieveDeviceId(): String =
        if (deviceId.isBlank()) {
            val id = UUID.randomUUID().toString()
            deviceId = id
            id
        } else {
            deviceId
        }
}
