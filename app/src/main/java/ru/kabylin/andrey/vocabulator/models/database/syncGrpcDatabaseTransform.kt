package ru.kabylin.andrey.vocabulator.models.database

import ru.kabylin.andrey.grpc.sync.SyncGrpcResponse
import ru.kabylin.andrey.grpc.sync.WordCategoryGrpcResponse
import ru.kabylin.andrey.grpc.sync.WordGrpcResponse

fun fromSyncGrpcResponseToSyncDatabaseModel(response: SyncGrpcResponse): SyncDatabaseModel {
    val categories = response.categoriesList.map(::fromCategoryGrpcResponseToCategoryDatabaseModel)
    val words = response.wordsList.map(::fromWordGrpcResponseToWordDatabaseModel)

    return SyncDatabaseModel(categories, words)
}

fun fromCategoryGrpcResponseToCategoryDatabaseModel(response: WordCategoryGrpcResponse): CategoryDatabaseModel =
    CategoryDatabaseModel(
        ref = response.id.toString(),
        name = response.name
    )

fun fromWordGrpcResponseToWordDatabaseModel(response: WordGrpcResponse): WordDatabaseModel {
    val details = if (response.pronounce.isNotBlank()) {
        listOf(
            DetailsDatabaseModel(
                title = "Pronounce",
                value = response.pronounce
            )
        )
    } else {
        listOf()
    }

    val definitions = response.definitionsList.map {
        DefinitionDatabaseModel(
            title = it.title,
            desc = it.desc,
            example = it.example,
            synonyms = it.synonyms
        )
    }

    return WordDatabaseModel(
        ref = response.id.toString(),
        categoryRef = response.categoryId.toString(),
        name = response.name,
        translations = response.translations,
        details = details,
        definitions = definitions
    )
}
