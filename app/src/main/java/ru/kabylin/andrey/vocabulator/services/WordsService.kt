package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.ui.models.WordDetailsItemVariant

interface WordsService {
    data class Category(
        val ref: String,
        val image: String?,
        val name: String
    )

    data class Word(
        val ref: String,
        val name: String,
        val score: Int
    )

    data class WordDetails(
        val ref: String,
        val name: String,
        val details: List<WordDetailsItemVariant>
    )

    fun getCategories(): Single<List<Category>>

    fun getWordsForCategory(categoryRef: String): Single<List<Word>>

    enum class Title {
        WORD,
        TRANSLATION,
        TRANSLATION_OR_DEFINITION,
    }

    fun getTrainWordsForCategory(categoryRef: String, title: Title): Single<List<Word>>

    fun getWordDetails(ref: String, addWordTitle: Boolean): Single<WordDetails>

    data class CategoryScore(
        val score: Int,
        val count: Int
    )

    fun getScoresCounts(categoryRef: String): Single<List<CategoryScore>>

    fun getScoreForWord(wordRef: String): Single<Int>

    data class NewWord(
        val name: String,
        val translation: String
    )

    fun addNewWord(newWord: NewWord): Completable
}
