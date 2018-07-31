package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable

class LocalScoreService : ScoreService {
    override fun rightWord(mode: TrainService.Mode, wordRef: String): Completable =
        Completable.complete()

    override fun wrongWord(mode: TrainService.Mode, wordRef: String): Completable =
        Completable.complete()
}
