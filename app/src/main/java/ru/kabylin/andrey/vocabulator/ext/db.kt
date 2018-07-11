package ru.kabylin.andrey.vocabulator.ext

import org.jetbrains.anko.db.MapRowParser

fun <T> mapRowParser(parser: (Map<String, Any?>) -> T): MapRowParser<T> =
    object : MapRowParser<T> {
        override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
    }
