package ru.kabylin.andrey.vocabulator.database.migrations

import android.database.sqlite.SQLiteDatabase

interface DatabaseMigration {
    val toVersion: Int

    fun onUpgrade(db: SQLiteDatabase)
}
