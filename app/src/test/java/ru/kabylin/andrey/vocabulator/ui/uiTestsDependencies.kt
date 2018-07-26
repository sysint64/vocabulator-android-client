package ru.kabylin.andrey.vocabulator.ui

import android.content.Context
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import ru.kabylin.andrey.vocabulator.compositors.Compositor
import ru.kabylin.andrey.vocabulator.compositors.EmptyCompositor
import ru.kabylin.andrey.vocabulator.dependencies
import ru.kabylin.andrey.vocabulator.mockServicesDependencies

fun uiTestsDependencies(context: Context) = Kodein.Module {
    import(dependencies(context))
    bind<Compositor>(tag = "client", overrides = true) with singleton { EmptyCompositor() }
    import(mockServicesDependencies, allowOverride = true)
}
