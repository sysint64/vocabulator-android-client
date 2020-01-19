package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.Settings

class SettingsServiceImpl : SettingsService {
    override fun getServerUrl(): Single<String> =
        Single.fromCallable {
            Settings.serverUrl
        }

    override fun setServerUrl(newUrl: String): Completable =
        Completable.fromAction {
            Settings.serverUrl = newUrl
        }

    override fun getServerPort(): Single<Int> =
        Single.fromCallable {
            Settings.serverPort
        }

    override fun setServerPort(newPort: Int): Completable =
        Completable.fromAction {
            Settings.serverPort = newPort
        }
}
