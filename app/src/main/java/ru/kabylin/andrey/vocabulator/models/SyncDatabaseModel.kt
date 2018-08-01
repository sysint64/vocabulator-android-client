package ru.kabylin.andrey.vocabulator.models

data class SyncDatabaseModel(
    val categories: List<CategoryDatabaseModel>,
    val words: List<WordDatabaseModel>
)

data class CategoryDatabaseModel(
    val ref: String,
    val name: String
)

data class WordDatabaseModel(
    val ref: String,
    val name: String,
    val translations: String,
    val details: List<DetailsDatabaseModel>,
    val definitions: List<DefinitionDatabaseModel>
)

data class DefinitionDatabaseModel(
    val title: String,
    val desc: String,
    val example: String,
    val synonyms: String
)

data class DetailsDatabaseModel(
    val title: String,
    val value: String
)
