package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Single

interface WordsService {
    data class Category(
        val ref: String,
        val image: String?,
        val name: String
    )

    data class Word(
        val ref: String,
        val name: String
    )

    data class TitleValue(
        val title: String,
        val value: String
    )

    data class WordDetails(
        val ref: String,
        val details: List<TitleValue>
    )

    fun getCategories(): Single<List<Category>>

    fun getWordsForCategory(): Single<List<Word>>

    fun getWordDetails(ref: String): Single<WordDetails>
}
