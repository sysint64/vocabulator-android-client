package ru.kabylin.andrey.vocabulator.services

import ru.kabylin.andrey.vocabulator.models.TitleValue
import ru.kabylin.andrey.vocabulator.models.database.CategoryDatabaseModel
import ru.kabylin.andrey.vocabulator.models.database.WordDatabaseModel

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

fun fromWordDatabaseModelToWordsServiceWord(model: WordDatabaseModel): WordsService.Word =
    WordsService.Word(
        ref = model.ref,
        name = model.name,
        score = 0
    )

fun fromWordDatabaseModelToWordsServiceWordDetails(model: WordDatabaseModel): WordsService.WordDetails =
    WordsService.WordDetails(
        ref = model.ref,
        name = model.name,
        translations = model.translations.split(",").map { it.trim() }.filter { it.isNotBlank() },
        details = model.details.map {
            TitleValue(
                title = it.title,
                value = it.value
            )
        },
        definitions = model.definitions.map {
            WordsService.Definition(
                title = it.title,
                desc = it.desc,
                example = it.example,
                synonyms = it.synonyms.split(",").map { it.trim() }.filter { it.isNotBlank() }
            )
        }
    )
