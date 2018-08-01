package ru.kabylin.andrey.vocabulator.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*
import ru.kabylin.andrey.vocabulator.database.migrations.DatabaseMigration

class SqlHelper(context: Context) : ManagedSQLiteOpenHelper(
    context,
    "storage_db_",
    version = 1
) {
    companion object {
        private var instance: SqlHelper? = null

        @Synchronized
        fun getInstance(context: Context): SqlHelper {
            if (instance == null) {
                instance = SqlHelper(context.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.transaction {
            db.createTable("Categories", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "ref" to TEXT + NOT_NULL,
                "name" to TEXT + NOT_NULL
            )
            db.createTable("Words", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "ref" to TEXT + NOT_NULL,
                "name" to TEXT + NOT_NULL,
                "translations" to TEXT + NOT_NULL,
                "details" to TEXT + NOT_NULL,
                "definitions" to TEXT + NOT_NULL
            )
            db.createTable("Scores", true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "word_id" to INTEGER + NOT_NULL,
                "score" to INTEGER + NOT_NULL
            )
            db.createTable("Meta", true,
                "device_id" to TEXT + NOT_NULL
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val migrations: List<DatabaseMigration> = listOf()

        for (migration in migrations) {
            if (oldVersion < migration.toVersion)
                migration.onUpgrade(db)
        }
    }
}
