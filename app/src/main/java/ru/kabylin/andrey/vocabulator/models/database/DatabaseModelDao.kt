package ru.kabylin.andrey.vocabulator.models.database

import android.arch.persistence.room.*

@Dao
interface DatabaseModelDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<CategoryDatabaseModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategories(categories: List<CategoryDatabaseModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllWords(words: List<WordDatabaseModel>)

    @Transaction
    fun insertAll(data: SyncDatabaseModel) {
        insertAllCategories(data.categories)
        insertAllWords(data.words)
    }
}
