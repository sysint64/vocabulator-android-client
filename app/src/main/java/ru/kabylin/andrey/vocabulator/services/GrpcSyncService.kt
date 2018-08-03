package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import ru.kabylin.andrey.grpc.sync.SyncGrpcRequest
import ru.kabylin.andrey.grpc.sync.SyncGrpc
import ru.kabylin.andrey.vocabulator.Settings
import ru.kabylin.andrey.vocabulator.client.http.HttpClient
import ru.kabylin.andrey.vocabulator.database.SyncDatabase
import ru.kabylin.andrey.vocabulator.ext.add
import ru.kabylin.andrey.vocabulator.ext.now
import ru.kabylin.andrey.vocabulator.models.database.fromSyncGrpcResponseToSyncDatabaseModel
import java.util.*

class GrpcSyncService(
    val client: HttpClient,
    private val database: SyncDatabase
) : SyncService {

    override fun sync(): Completable =
        Single.fromCallable {
            val stub = SyncGrpc.newBlockingStub(client.grpcChannel)
            val request = SyncGrpcRequest.newBuilder().build()

            stub.sync(request)
        }
            .map(::fromSyncGrpcResponseToSyncDatabaseModel)
            .map { databaseData ->
                Settings.nextSync = now().add(1, Calendar.DAY_OF_MONTH).time
                database.dao().insertAll(databaseData)
                // TODO: Синхронизация удаленных слов.
            }
            .toCompletable()
}