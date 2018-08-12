package ru.kabylin.andrey.vocabulator.models.database

import android.arch.persistence.room.*

@Entity(tableName = "categories", indices = [Index("ref", unique = true)])
data class CategoryDatabaseModel(
    @PrimaryKey
    @ColumnInfo(name = "ref")
    val ref: String,

    @ColumnInfo(name = "languageRef")
    val languageRef: String,

    @ColumnInfo(name = "name")
    val name: String
)
