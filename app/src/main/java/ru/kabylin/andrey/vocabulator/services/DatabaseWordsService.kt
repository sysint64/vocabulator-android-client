package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.database.SyncDatabase

class DatabaseWordsService(private val database: SyncDatabase) : WordsService {
    override fun getCategories(): Single<List<WordsService.Category>> =
        Single.fromCallable {
            database.dao().getAllCategories()
        }
            .map {
                it.map {
                    WordsService.Category(
                        ref = it.ref,
                        name = it.name,
                        image = null
                    )
                }
            }

    override fun getWordsForCategory(categoryRef: String): Single<List<WordsService.Word>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWordDetails(ref: String): Single<WordsService.WordDetails> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getScoresCounts(categoryRef: String): Single<List<WordsService.CategoryScore>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
