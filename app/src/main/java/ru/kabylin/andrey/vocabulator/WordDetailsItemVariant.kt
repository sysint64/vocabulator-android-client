package ru.kabylin.andrey.vocabulator

import ru.kabylin.andrey.vocabulator.services.WordsService

data class WordDetailsItemVariant(
    val title: String? = null,
    val listItem: String? = null,
    val separator: Unit? = null,
    val definition: WordsService.Definition? = null,
    val desc: String? = null
)
