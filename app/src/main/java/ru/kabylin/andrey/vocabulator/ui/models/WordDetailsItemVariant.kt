package ru.kabylin.andrey.vocabulator.ui.models

data class WordDetailsDefinition(
    val title: String,
    val desc: String,
    val example: String,
    val synonyms: List<String>
)

data class Kanji(
    val hieroglyph: String,
    val reading: String,
    val meaning: String
)

data class WordDetailsItemVariant(
    val title: String? = null,
    val listItem: String? = null,
    val separator: Unit? = null,
    val definition: WordDetailsDefinition? = null,
    val desc: String? = null,
    val kanji: Kanji? = null
)
