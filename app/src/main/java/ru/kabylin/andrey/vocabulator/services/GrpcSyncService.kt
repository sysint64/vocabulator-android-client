package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import ru.kabylin.andrey.grpc.sync.NewWordGrpcRequest
import ru.kabylin.andrey.grpc.sync.SyncGrpcRequest
import ru.kabylin.andrey.grpc.sync.SyncGrpc
import ru.kabylin.andrey.grpc.sync.WordGrpcRequest
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
            val request = SyncGrpcRequest.newBuilder()
            var index = 0

            for (word in database.dao().getAllWords()) {
                val delta = word.score - word.lastScore

                if (delta == 0)
                    continue

                val wordRequest = WordGrpcRequest.newBuilder()
                    .setId(word.ref.toLong())
                    .setScoreDelta(delta)
                    .build()

                request.addWords(index, wordRequest)
                index += 1
            }

            index = 0

            for (newWord in database.dao().getAllNewWords()) {
                val newWordRequest = NewWordGrpcRequest.newBuilder()
                    .setName(newWord.name)
                    .setTranslation(newWord.translation)
                    .build()

                request.addNewWords(index, newWordRequest)
                index += 1
            }

            stub.sync(request.build())
        }
            .map(::fromSyncGrpcResponseToSyncDatabaseModel)
            .map { databaseData ->
                Settings.nextSync = now().add(1, Calendar.DAY_OF_MONTH).time
                database.dao().sync(databaseData)
                // TODO: Синхронизация удаленных слов.
            }
            .toCompletable()
}
