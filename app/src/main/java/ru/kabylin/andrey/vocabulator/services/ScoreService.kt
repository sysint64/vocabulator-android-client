package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable

interface ScoreService {
    /**
     * Пользователь верно угадал слово (нажал на кнопку right)
     */
    fun rightWord(mode: TrainService.Mode, wordRef: String): Completable

    /**
     * Пользователь не верно угадал слово (нажал на кнопку wrong)
     */
    fun wrongWord(mode: TrainService.Mode, wordRef: String): Completable
}
