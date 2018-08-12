package ru.kabylin.andrey.vocabulator.services

import ru.kabylin.andrey.vocabulator.models.database.CategoryDatabaseModel
import ru.kabylin.andrey.vocabulator.models.database.WordDatabaseModel
import ru.kabylin.andrey.vocabulator.ui.models.Kanji
import ru.kabylin.andrey.vocabulator.ui.models.WordDetailsDefinition
import ru.kabylin.andrey.vocabulator.ui.models.WordDetailsItemVariant

fun fromListCategoryDatabaseModelToListWordsServiceCategory(list: List<CategoryDatabaseModel>): List<WordsService.Category> =
    list.map(::fromCategoryDatabaseModelToWordsServiceCategory)

fun fromCategoryDatabaseModelToWordsServiceCategory(model: CategoryDatabaseModel): WordsService.Category =
    WordsService.Category(
        ref = model.ref,
        name = model.name,
        image = null
    )

fun fromListWordDatabaseModelToListWordsServiceWord(list: List<WordDatabaseModel>): List<WordsService.Word> =
    list.map(::fromWordDatabaseModelToWordsServiceWord)

fun getNormalizedScore(score: Int) =
    if (score == 0) {
        0
    } else {
        // Clamp value range in 1 to 10
        minOf(maxOf(score / 10, 1), 10)
    }

fun fromWordDatabaseModelToWordsServiceWord(model: WordDatabaseModel): WordsService.Word {
    return WordsService.Word(
        ref = model.ref,
        name = model.name,
        score = getNormalizedScore(model.score)
    )
}

fun fromWordDatabaseModelToWordsServiceWordDetails(model: WordDatabaseModel): WordsService.WordDetails {
    val details = ArrayList<WordDetailsItemVariant>()

    for (detailItem in model.details) {
        details.add(WordDetailsItemVariant(title = detailItem.title))
        details.add(WordDetailsItemVariant(desc = detailItem.value))
    }

    val translations = model.translations.split(";").map { it.trim() }.filter { it.isNotBlank() }

    if (translations.isNotEmpty())
        details.add(WordDetailsItemVariant(title = "Translations"))

    for (translation in translations)
        details.add(WordDetailsItemVariant(listItem = translation))

    for (definition in model.definitions)
        details.add(
            WordDetailsItemVariant(
                definition = WordDetailsDefinition(
                    title = definition.title,
                    desc = definition.desc,
                    example = definition.example,
                    synonyms = definition.synonyms.split(",").map { it.trim() }.filter { it.isNotBlank() }
                )
            )
        )

    if (model.kanji.isNotEmpty())
        details.add(WordDetailsItemVariant(title = "Kanji"))

    for (kanji in model.kanji.reversed()) {
        details.add(
            WordDetailsItemVariant(
                kanji = Kanji(
                    hieroglyph = kanji.hieroglyph,
                    reading = kanji.reading,
                    meaning = kanji.meaning
                )
            )
        )
    }

    return WordsService.WordDetails(
        ref = model.ref,
        name = model.name,
        details = details
    )
}
