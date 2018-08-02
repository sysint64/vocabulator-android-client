package ru.kabylin.andrey.vocabulator.models.database

import android.arch.persistence.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "words", indices = [Index("ref", unique = true)])
data class WordDatabaseModel(
    @PrimaryKey
    @ColumnInfo(name = "ref")
    val ref: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "translations")
    val translations: String,

    @ColumnInfo(name = "details")
    val details: List<DetailsDatabaseModel>,

    @ColumnInfo(name = "definitions")
    val definitions: List<DefinitionDatabaseModel>
)

data class DefinitionDatabaseModel(
    val title: String,
    val desc: String,
    val example: String,
    val synonyms: String
)

data class DetailsDatabaseModel(
    val title: String,
    val value: String
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