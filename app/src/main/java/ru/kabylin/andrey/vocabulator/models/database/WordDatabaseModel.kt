package ru.kabylin.andrey.vocabulator.models.database

import android.arch.persistence.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "words", indices = [Index("ref", unique = true)])
data class WordDatabaseModel(
    @PrimaryKey
    @ColumnInfo(name = "ref")
    val ref: String,

    @ColumnInfo(name = "category_ref")
    val categoryRef: String,

    @ColumnInfo(name = "language_ref")
    val languageRef: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "translations")
    val translations: String,

    @ColumnInfo(name = "details")
    val details: List<DetailsDatabaseModel>,

    @ColumnInfo(name = "definitions")
    val definitions: List<DefinitionDatabaseModel>,

    @ColumnInfo(name = "score")
    val score: Int,

    @ColumnInfo(name = "lastScore")
    val lastScore: Int,

    @ColumnInfo(name = "association_image")
    val associationImage: String,

    @ColumnInfo(name = "examples")
    val examples: List<ExampleDatabaseModel>,

    @ColumnInfo(name = "kanji")
    val kanji: List<KanjiDatabaseModel>
)

data class DefinitionDatabaseModel(
    val title: String,
    val desc: String,
    val example: String,
    val translation: String,
    val synonyms: String
)

data class DetailsDatabaseModel(
    val title: String,
    val value: String
)

data class ExampleDatabaseModel(
    val content: String,
    val translation: String
)

data class KanjiDatabaseModel(
    val hieroglyph: String,
    val reading: String,
    val meaning: String
)

class DetailsTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun toString(details: List<DetailsDatabaseModel>): String {
        return gson.toJson(details)
    }

    @TypeConverter
    fun toModel(data: String): List<DetailsDatabaseModel> {
        val type = object : TypeToken<ArrayList<DetailsDatabaseModel>>() {}.type
        return gson.fromJson(data, type)
    }
}

class DefinitionTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun toString(details: List<DefinitionDatabaseModel>): String {
        return gson.toJson(details)
    }

    @TypeConverter
    fun toModel(data: String): List<DefinitionDatabaseModel> {
        val type = object : TypeToken<ArrayList<DefinitionDatabaseModel>>() {}.type
        return gson.fromJson(data, type)
    }
}

class ExampleTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun toString(details: List<ExampleDatabaseModel>): String {
        return gson.toJson(details)
    }

    @TypeConverter
    fun toModel(data: String): List<ExampleDatabaseModel> {
        val type = object : TypeToken<ArrayList<ExampleDatabaseModel>>() {}.type
        return gson.fromJson(data, type)
    }
}

class KanjiTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun toString(details: List<KanjiDatabaseModel>): String {
        return gson.toJson(details)
    }

    @TypeConverter
    fun toModel(data: String): List<KanjiDatabaseModel> {
        val type = object : TypeToken<ArrayList<KanjiDatabaseModel>>() {}.type
        return gson.fromJson(data, type)
    }
}
