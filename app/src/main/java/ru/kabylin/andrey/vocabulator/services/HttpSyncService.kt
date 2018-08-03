package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.GET
import ru.kabylin.andrey.vocabulator.Settings
import ru.kabylin.andrey.vocabulator.client.http.HttpClient
import ru.kabylin.andrey.vocabulator.database.SyncDatabase
import ru.kabylin.andrey.vocabulator.ext.add
import ru.kabylin.andrey.vocabulator.ext.now
import ru.kabylin.andrey.vocabulator.models.database.fromSyncResponseToSyncDatabaseModel
import ru.kabylin.andrey.vocabulator.models.http.SyncResponse
import java.util.*

class HttpSyncService(
    client: HttpClient,
    private val database: SyncDatabase
) : SyncService {

    interface ApiGateway {
        @GET("sync/")
        fun sync(): Single<SyncResponse>
    }

    private val apiGateway =
        client.createRetrofitGateway(
            ApiGateway::class.java,
            HttpClient.Dest.MAIN_API
        )

    override fun sync(): Completable =
        apiGateway.sync()
            .map(::fromSyncResponseToSyncDatabaseModel)
            .map { databaseData ->
                Settings.nextSync = now().add(1, Calendar.DAY_OF_MONTH).time
                database.dao().insertAll(databaseData)
                // TODO: Синхронизация удаленных слов.
            }
            .toCompletable()
}
