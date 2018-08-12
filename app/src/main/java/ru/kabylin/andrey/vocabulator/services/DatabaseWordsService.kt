package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.database.SyncDatabase
import ru.kabylin.andrey.vocabulator.models.database.NewWordDatabaseModel

class DatabaseWordsService(private val database: SyncDatabase) : WordsService {
    override fun getCategories(): Single<List<WordsService.Category>> =
        Single.fromCallable { database.dao().getAllCategories() }
            .map(::fromListCategoryDatabaseModelToListWordsServiceCategory)

    override fun getWordsForCategory(categoryRef: String): Single<List<WordsService.Word>> =
        Single.fromCallable { database.dao().getWordsForCategory(categoryRef) }
            .map(::fromListWordDatabaseModelToListWordsServiceWord)
            .map {
                it.sortedBy {
                    if (it.score == 0) 11 else it.score
                }
            }

    override fun getTrainWordsForCategory(categoryRef: String): Single<List<WordsService.Word>> =
        Single.fromCallable { database.dao().getWordsForCategory(categoryRef) }
            .map(::fromListWordDatabaseModelToListWordsServiceWord)
            .map {
                it.sortedBy {
                    if (it.score == 0) 5 else it.score
                }
            }

    override fun getWordDetails(ref: String): Single<WordsService.WordDetails> =
        Single.fromCallable { database.dao().getWord(ref) }
            .map(::fromWordDatabaseModelToWordsServiceWordDetails)

    override fun getScoresCounts(categoryRef: String): Single<List<WordsService.CategoryScore>> =
        getWordsForCategory(categoryRef)
            .map { words ->
                (0..10).map { score ->
                    WordsService.CategoryScore(
                        score = score,
                        count = words.count { it.score == score }
                    )
                }
            }

    override fun getScoreForWord(wordRef: String): Single<Int> =
        Single.fromCallable {
            database.dao().getWordScore(wordRef)
        }

    override fun addNewWord(newWord: WordsService.NewWord): Completable =
        Completable.fromAction {
            database.dao().insertNewWord(
                NewWordDatabaseModel(
                    id = 0,
                    name = newWord.name,
                    translation = newWord.translation
                )
            )
        }
}
