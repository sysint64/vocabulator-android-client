package ru.kabylin.andrey.vocabulator

import io.mockk.mockk
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton
import ru.kabylin.andrey.vocabulator.services.ScoreService
import ru.kabylin.andrey.vocabulator.services.TrainService
import ru.kabylin.andrey.vocabulator.services.WordsService

val mockServicesDependencies = Kodein.Module {
    bind<WordsService>(overrides = true) with singleton { mockk<WordsService>(relaxed = true) }
    bind<TrainService>(overrides = true) with singleton { mockk<TrainService>(relaxed = true) }
    bind<ScoreService>(overrides = true) with singleton { mockk<ScoreService>(relaxed = true) }
}
