package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single

interface LanguagesService {
    data class Language(
        val ref: String,
        val name: String
    )

    fun getCurrentLanguage() : Single<String>

    fun getLanguages(): Single<List<Language>>

    fun selectLanguage(languageRef: String): Completable
}
