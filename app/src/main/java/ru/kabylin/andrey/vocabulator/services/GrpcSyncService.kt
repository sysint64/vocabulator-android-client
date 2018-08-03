package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import ru.kabylin.andrey.vocabulator.client.http.HttpClient
import ru.kabylin.andrey.vocabulator.database.SyncDatabase

class GrpcSyncService(
    client: HttpClient,
    private val database: SyncDatabase
) : SyncService {

    override fun sync(): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
