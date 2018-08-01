package ru.kabylin.andrey.vocabulator.models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
    val details = listOf(
        DetailsDatabaseModel(
            title = "Pronounce",
            value = response.pronounce
        )
    )

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
        name = response.name,
        translations = response.translation,
        details = details,
        definitions = definitions
    )
}

fun fromCategoryRawDatabaseRowToCategoryDatabaseModel(map: Map<String, Any?>): CategoryDatabaseModel =
    CategoryDatabaseModel(
        ref = map["ref"] as String,
        name = map["name"] as String
    )

fun fromWordRawDatabaseRowToWordDatabaseModel(map: Map<String, Any?>): WordDatabaseModel {
    val gson = Gson()

    val detailsDatabaseModelType = object : TypeToken<ArrayList<DetailsDatabaseModel>>() {}.type
    val definitionDatabaseModelType = object : TypeToken<ArrayList<DefinitionDatabaseModel>>() {}.type

    return WordDatabaseModel(
        ref = map["ref"] as String,
        name = map["ref"] as String,
        translations = map["translations"] as String,
        details = gson.fromJson(map["details"] as String, detailsDatabaseModelType),
        definitions = gson.fromJson(map["definitions"] as String, definitionDatabaseModelType)
    )
}
