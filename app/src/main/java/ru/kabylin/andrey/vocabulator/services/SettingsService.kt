package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single

interface SettingsService {
    fun getServerUrl(): Single<String>

    fun setServerUrl(newUrl: String): Completable

    fun getServerPort(): Single<Int>

    fun setServerPort(newPort: Int): Completable
}
