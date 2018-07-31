package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable

interface SyncService {
    fun sync(): Completable
}
