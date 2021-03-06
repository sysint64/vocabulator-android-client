package ru.kabylin.andrey.vocabulator

import android.arch.persistence.room.Room
import android.content.Context
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.client.http.HttpClient
import ru.kabylin.andrey.vocabulator.client.http.HttpClientCompositor
import ru.kabylin.andrey.vocabulator.compositors.Compositor
import ru.kabylin.andrey.vocabulator.database.SyncDatabase
import ru.kabylin.andrey.vocabulator.services.*

fun dependencies(context: Context) = Kodein.Module {
    bind<Client>() with singleton { HttpClient }
    bind<Compositor>("client") with provider {
        HttpClientCompositor(client = instance<Client>() as HttpClient)
    }
    bind<DataStorage>() with singleton { HttpClient.dataState }

    bind<SyncDatabase>(tag = "storage") with provider {
        Room.databaseBuilder(context, SyncDatabase::class.java, "sync_storage").build()
    }

    bind<LanguagesService>() with singleton {
        DatabaseLanguagesService(
            database = instance("storage")
        )
    }

    bind<WordsService>() with singleton {
        DatabaseWordsService(
            database = instance("storage"),
            languagesService = DatabaseLanguagesService(
                database = instance("storage")
            )
        )
    }
    bind<TrainService>() with singleton {
        RealTrainService(
            wordsService = instance(),
            scoreService = instance()
        )
    }
    bind<ScoreService>() with singleton {
        DatabaseScoreService(
            database = instance("storage"),
            wordsService = instance()
        )
    }
    bind<SyncService>() with singleton {
        GrpcSyncService(instance<Client>() as HttpClient,
            database = instance("storage")
        )
    }

    bind<SettingsService>() with singleton {
        SettingsServiceImpl()
    }
}
