package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.client.http.HttpClient

class HttpWordsService(client: HttpClient) : WordsService {
    override fun getCategories(): Single<List<WordsService.Category>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWordsForCategory(): Single<List<WordsService.Word>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getWordDetails(ref: String): Single<WordsService.WordDetails> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
