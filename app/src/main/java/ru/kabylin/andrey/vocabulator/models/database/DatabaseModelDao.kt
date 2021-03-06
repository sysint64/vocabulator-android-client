package ru.kabylin.andrey.vocabulator.models.database

import android.arch.persistence.room.*

@Dao
interface DatabaseModelDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<CategoryDatabaseModel>

    @Query("SELECT * FROM categories WHERE languageRef = :languageRef")
    fun getCategoriesForLanguage(languageRef: String): List<CategoryDatabaseModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllLanguages(languages: List<LanguageDatabaseModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategories(categories: List<CategoryDatabaseModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllWords(words: List<WordDatabaseModel>)

    @Transaction
    fun sync(data: SyncDatabaseModel) {
        insertAllLanguages(data.languages)
        insertAllCategories(data.categories)
        insertAllWords(data.words)
        clearNewWords()
    }

    @Query("SELECT * FROM languages")
    fun getAllLanguages(): List<LanguageDatabaseModel>

    @Query("SELECT * FROM words WHERE category_ref = :categoryRef")
    fun getWordsForCategory(categoryRef: String): List<WordDatabaseModel>

    @Query("SELECT * FROM words WHERE language_ref = :languageRef")
    fun getWordsForLanguage(languageRef: String): List<WordDatabaseModel>

    @Query("SELECT * FROM words WHERE ref = :ref LIMIT 1")
    fun getWord(ref: String): WordDatabaseModel

    @Query("SELECT score FROM words WHERE ref = :ref LIMIT 1")
    fun getWordScore(ref: String): Int

    @Query("SELECT * FROM words")
    fun getAllWords(): List<WordDatabaseModel>

    @Query("UPDATE words SET score = :newScore  WHERE ref = :ref")
    fun updateWordScore(ref: String, newScore: Int): Int

    @Query("SELECT * FROM new_words")
    fun getAllNewWords(): List<NewWordDatabaseModel>

    @Query("DELETE FROM new_words")
    fun clearNewWords(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewWord(data: NewWordDatabaseModel)
}
