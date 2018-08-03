package ru.kabylin.andrey.vocabulator.models.database

import ru.kabylin.andrey.vocabulator.models.http.CategoryResponse
import ru.kabylin.andrey.vocabulator.models.http.SyncResponse
import ru.kabylin.andrey.vocabulator.models.http.WordResponse

fun fromSyncResponseToSyncDatabaseModel(response: SyncResponse): SyncDatabaseModel {
    val categories = response.categories.map(::fromCategoryResponseToCategoryDatabaseModel)
    val words = response.words.map(::fromWordResponseToWordDatabaseModel)

    return SyncDatabaseModel(categories, words)
}

fun fromCategoryResponseToCategoryDatabaseModel(response: CategoryResponse): CategoryDatabaseModel =
    CategoryDatabaseModel(
        ref = response.id.toString(),
        name = response.name
    )

fun fromWordResponseToWordDatabaseModel(response: WordResponse): WordDatabaseModel {
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

    val definitions = response.definitions.map {
        DefinitionDatabaseModel(
            title = it.title,
            desc = it.desc,
            example = it.example,
            synonyms = it.synonyms
        )
    }

    return WordDatabaseModel(
        ref = response.id.toString(),
        categoryRef = response.category.toString(),
        name = response.name,
        translations = response.translation,
        details = details,
        definitions = definitions
    )
}
