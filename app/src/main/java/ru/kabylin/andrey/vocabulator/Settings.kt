package ru.kabylin.andrey.vocabulator

import com.chibatching.kotpref.KotprefModel

object Settings : KotprefModel() {
    var nextSync: Long by longPref(default = 0L)
}
