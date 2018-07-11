package ru.kabylin.andrey.vocabulator

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
import ru.kabylin.andrey.vocabulator.services.DataStorage

fun dependencies(context: Context) = Kodein.Module {
    bind<Client>() with singleton { HttpClient }
    bind<Compositor>("client") with provider {
        HttpClientCompositor(client = instance<Client>() as HttpClient)
    }
    bind<DataStorage>() with singleton { HttpClient.dataState }
}
