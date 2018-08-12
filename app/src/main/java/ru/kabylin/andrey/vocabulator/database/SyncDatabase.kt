package ru.kabylin.andrey.vocabulator.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import ru.kabylin.andrey.vocabulator.models.database.*

@Database(
    version = 1,
    entities = [
        CategoryDatabaseModel::class,
        WordDatabaseModel::class,
        NewWordDatabaseModel::class,
        LanguageDatabaseModel::class
    ]
)
@TypeConverters(
    value = [
        DetailsTypeConverter::class,
        DefinitionTypeConverter::class,
        ExampleTypeConverter::class,
        KanjiTypeConverter::class
    ]
)
abstract class SyncDatabase : RoomDatabase() {
    abstract fun dao(): DatabaseModelDao
}
