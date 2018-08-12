package ru.kabylin.andrey.vocabulator.models.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "languages", indices = [(Index("ref", unique = true))])
data class LanguageDatabaseModel(
    @PrimaryKey
    @ColumnInfo(name = "ref")
    val ref: String,

    @ColumnInfo(name = "name")
    val name: String
)
