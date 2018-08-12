package ru.kabylin.andrey.vocabulator.models.database

data class SyncDatabaseModel(
    val languages: List<LanguageDatabaseModel>,
    val categories: List<CategoryDatabaseModel>,
    val words: List<WordDatabaseModel>
)
