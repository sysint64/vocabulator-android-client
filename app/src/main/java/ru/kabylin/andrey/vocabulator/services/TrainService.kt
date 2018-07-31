package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.Subject


interface TrainService {
    enum class Mode {
        WORD_TRANSLATION,
        TRANSLATION_WORD
    }

    fun startWordTranslation(categoryRef: String): Completable

    fun startTranslationWord(categoryRef: String): Completable

    data class Word(
        val ref: String,
        val name: String
    )

    fun nextWord(): Single<Word>

    fun currentWord(): Single<Word>

    data class WordStatus(
        val pos: Int,
        val isRight: Boolean
    )

    fun right(): Single<WordStatus>

    fun wrong(): Single<WordStatus>

    fun reveal(): Single<WordsService.WordDetails>

    fun newPageEvents(): Subject<Boolean>

    fun finishEvents(): Subject<Boolean>
}
