package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper
import org.jetbrains.anko.db.transaction
import retrofit2.http.GET
import ru.kabylin.andrey.vocabulator.Settings
import ru.kabylin.andrey.vocabulator.client.http.HttpClient
import ru.kabylin.andrey.vocabulator.ext.add
import ru.kabylin.andrey.vocabulator.ext.now
import java.util.*

class HttpSyncService(
    private val client: HttpClient,
    private val database: ManagedSQLiteOpenHelper
) : SyncService {

    interface ApiGateway {
        data class SyncRequest(
            val lastSync: Long
        )

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
            .map { response ->
                database.use {
                    transaction {
                    }
                }

                Settings.nextSync = now().add(1, Calendar.DAY_OF_MONTH).time
            }
            .toCompletable()
}
