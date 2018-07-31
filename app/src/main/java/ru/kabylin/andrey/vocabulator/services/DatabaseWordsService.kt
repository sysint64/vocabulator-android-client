package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Single

class DatabaseWordsService : WordsService {
    override fun getCategories(): Single<List<WordsService.Category>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
