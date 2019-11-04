package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.Subject


interface TrainService {
    enum class Mode {
        LEARNING,
        REVISION,
        RANDOM,
    }

    fun startTraining(): Completable

    fun setLanguage(languageRef: String): Completable

    fun setCategory(categoryRef: String): Completable

    fun setWordTitle(title: WordsService.Title): Completable

    fun setMode(mode: Mode): Completable

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
