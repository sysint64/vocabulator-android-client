package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.database.SyncDatabase
import ru.kabylin.andrey.vocabulator.models.database.NewWordDatabaseModel

class DatabaseWordsService(private val database: SyncDatabase) : WordsService {
    override fun getCategories(): Single<List<WordsService.Category>> =
        Single.fromCallable { database.dao().getAllCategories() }
            .map(::fromListCategoryDatabaseModelToListWordsServiceCategory)

    override fun getWordsForCategory(
        categoryRef: String,
        title: WordsService.Title,
        orderBy: WordsService.OrderBy
    ): Single<List<WordsService.Word>> =
        Single.fromCallable { database.dao().getWordsForCategory(categoryRef) }
            .map { fromListWordDatabaseModelToListWordsServiceWord(it, title) }
            .map { orderWords(it, orderBy) }

    override fun getWordsForLanguage(
        languageRef: String,
        title: WordsService.Title,
        orderBy: WordsService.OrderBy
    ): Single<List<WordsService.Word>> =
        Single.fromCallable { database.dao().getWordsForLanguage(languageRef) }
            .map { fromListWordDatabaseModelToListWordsServiceWord(it, title) }
            .map { orderWords(it, orderBy) }

    private fun orderWords(
        words: List<WordsService.Word>,
        orderBy: WordsService.OrderBy
    ): List<WordsService.Word> =
        when (orderBy) {
            WordsService.OrderBy.SCORE ->
                // NOTE: 0 means that it's a new word
                words.sortedBy { if (it.score == 0) 11 else it.score }

            WordsService.OrderBy.REVISION_MODE ->
                words.sortedBy { if (it.score == 0) 5 else it.score }

            WordsService.OrderBy.LEARNING_MODE ->
                words.sortedBy { it.score }

            WordsService.OrderBy.RANDOM ->
                words.shuffled()
        }

    override fun getWordDetails(ref: String, addWordTitle: Boolean): Single<WordsService.WordDetails> =
        Single.fromCallable { database.dao().getWord(ref) }
            .map { fromWordDatabaseModelToWordsServiceWordDetails(it, addWordTitle) }

    override fun getScoresCounts(categoryRef: String): Single<List<WordsService.CategoryScore>> =
        getWordsForCategory(categoryRef, WordsService.Title.WORD, WordsService.OrderBy.SCORE)
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
