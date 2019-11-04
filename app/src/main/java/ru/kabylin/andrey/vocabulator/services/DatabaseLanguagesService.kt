package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.Settings
import ru.kabylin.andrey.vocabulator.database.SyncDatabase

class DatabaseLanguagesService(private val database: SyncDatabase) : LanguagesService {
    override fun getCurrentLanguage(): Single<String> =
        Single.fromCallable {
            if (Settings.currentLanguage != "") {
                Settings.currentLanguage
            } else {
                database.dao().getAllLanguages().firstOrNull()?.ref ?: ""
            }
        }

    override fun getLanguages(): Single<List<LanguagesService.Language>> =
        Single.fromCallable {
            database.dao().getAllLanguages()
                .map(::fromLanguageDatabaseModelToLanguagesServiceLanguage)
        }


    override fun selectLanguage(languageRef: String): Completable =
        Completable.fromAction {
            Settings.currentLanguage = languageRef
        }

}
