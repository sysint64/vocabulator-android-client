package ru.kabylin.andrey.vocabulator.models.database

import ru.kabylin.andrey.grpc.sync.*

fun fromSyncGrpcResponseToSyncDatabaseModel(response: SyncGrpcResponse): SyncDatabaseModel {
    val languages = response.languagesList.map(::fromLanguageGrpcResponseToLanguageDatabaseModel)
    val categories = response.categoriesList.map(::fromCategoryGrpcResponseToCategoryDatabaseModel)
    val words = response.wordsList.map(::fromWordGrpcResponseToWordDatabaseModel)

    return SyncDatabaseModel(
        languages,
        categories,
        words
    )
}

fun fromLanguageGrpcResponseToLanguageDatabaseModel(response: LangaugeGrpcResponse): LanguageDatabaseModel =
    LanguageDatabaseModel(
        ref = response.id.toString(),
        name = response.name
    )

fun fromCategoryGrpcResponseToCategoryDatabaseModel(response: WordCategoryGrpcResponse): CategoryDatabaseModel =
    CategoryDatabaseModel(
        ref = response.id.toString(),
        name = response.name,
        languageRef = response.languageId.toString()
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
            synonyms = it.synonyms,
            translation = it.translation
        )
    }

    return WordDatabaseModel(
        ref = response.id.toString(),
        categoryRef = response.categoryId.toString(),
        languageRef = response.langaugeId.toString(),
        name = response.name,
        translations = response.translations,
        details = details,
        definitions = definitions,
        score = response.score.toInt(),
        lastScore = response.score.toInt(),
        associationImage = response.associationImage,
        examples = response.examplesList.map(::fromExampleGrpcResponseToExampleDatabaseModel),
        kanji = response.kanjiList.map(::fromKanjiGrpcResponseToKanjiDatabaseModel)
    )
}

fun fromExampleGrpcResponseToExampleDatabaseModel(response: ExampleGrpcResponse): ExampleDatabaseModel =
    ExampleDatabaseModel(
        content = response.content,
        translation = response.translation
    )

fun fromKanjiGrpcResponseToKanjiDatabaseModel(response: KanjiGrpcResponse): KanjiDatabaseModel =
    KanjiDatabaseModel(
        hieroglyph = response.hieroglyph,
        reading = response.reading,
        meaning = response.meaning
    )
