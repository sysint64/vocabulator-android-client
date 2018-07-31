package ru.kabylin.andrey.vocabulator.services

data class SyncResponse(
    val categories: List<CategoryResponse>,
    val words: List<WordResponse>
)

data class CategoryResponse(
    val id: Long,
    val name: String
)

data class WordResponse(
    val id: Long,
    val category: Long,
    val name: String,
    val translation: String,
    val pronounce: String,
    val definitions: List<DefinitionResponse>
)

data class DefinitionResponse(
    val title: String,
    val desc: String,
    val example: String,
    val synonyms: String
)
