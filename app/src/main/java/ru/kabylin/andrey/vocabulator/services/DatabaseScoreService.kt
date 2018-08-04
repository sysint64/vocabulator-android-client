package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import ru.kabylin.andrey.vocabulator.database.SyncDatabase

class DatabaseScoreService(
    private val database: SyncDatabase,
    private val wordsService: WordsService
) : ScoreService {

    companion object {
        val rightDeltas = mapOf(
            0 to 10,
            1 to 5,
            2 to 5,
            3 to 5,
            4 to 3,
            5 to 3,
            6 to 3,
            7 to 3,
            8 to 2,
            9 to 2
        )

        val wrongDeltas = mapOf(
            1 to 2,
            2 to 2,
            3 to 3,
            4 to 3,
            5 to 3,
            6 to 3,
            7 to 5,
            8 to 5,
            9 to 5,
            10 to 10
        )
    }

    private fun updateScore(wordRef: String, newScore: Int) =
        Completable.fromAction {
            // Score should be in range from 1 to 100, 0 - means that word is new.
            val score = minOf(maxOf(newScore, 1), 100)
            database.dao().updateWordScore(wordRef, score)
        }

    override fun rightWord(mode: TrainService.Mode, wordRef: String): Completable =
        wordsService.getScoreForWord(wordRef)
            .flatMapCompletable {
                val delta = rightDeltas[getNormalizedScore(it)] ?: 3
                updateScore(wordRef, it + delta)
            }

    override fun wrongWord(mode: TrainService.Mode, wordRef: String): Completable =
        wordsService.getScoreForWord(wordRef)
            .flatMapCompletable {
                val delta = wrongDeltas[getNormalizedScore(it)] ?: 3
                updateScore(wordRef, it - delta)
            }
}
