package ru.kabylin.andrey.vocabulator

import android.content.Context
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import ru.kabylin.andrey.vocabulator.client.Client
import ru.kabylin.andrey.vocabulator.client.http.HttpClient
import ru.kabylin.andrey.vocabulator.client.http.HttpClientCompositor
import ru.kabylin.andrey.vocabulator.compositors.Compositor
import ru.kabylin.andrey.vocabulator.database.SqlHelper
import ru.kabylin.andrey.vocabulator.services.*

fun dependencies(context: Context) = Kodein.Module {
    bind<Client>() with singleton { HttpClient }
    bind<Compositor>("client") with provider {
        HttpClientCompositor(client = instance<Client>() as HttpClient)
    }
    bind<DataStorage>() with singleton { HttpClient.dataState }

    bind<ManagedSQLiteOpenHelper>(tag = "storage") with provider {
        SqlHelper.getInstance(context)
    }

    bind<WordsService>() with singleton { LocalWordsService(instance<Client>() as HttpClient) }
    bind<TrainService>() with singleton {
        HttpTrainService(
            wordsService = instance(),
            scoreService = instance()
        )
    }
    bind<ScoreService>() with singleton {
        LocalScoreService()
    }
    bind<SyncService>() with singleton {
        HttpSyncService(instance<Client>() as HttpClient,
            database = instance("storage")
        )
    }
}
