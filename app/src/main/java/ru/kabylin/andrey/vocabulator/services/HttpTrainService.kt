package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import ru.kabylin.andrey.vocabulator.client.http.HttpClient

class HttpTrainService(
    client: HttpClient,
    val wordsService: WordsService
) : TrainService {

    companion object {
        const val PAGE_SIZE = 2
    }

    enum class Mode {
        WORD_TRANSLATION,
        TRANSLATION_WORD
    }

    private var mode = Mode.WORD_TRANSLATION
    private var wordsToLearn = ArrayList<TrainService.Word>()
    private var pages = ArrayList<ArrayList<Int>>()
    private var currentPageIndex: Int = 0
    private val newPageEventsSubject = PublishSubject.create<Boolean>()
    private val finishEvents = PublishSubject.create<Boolean>()
    private var currentWordIndexRelativePage = 0

    private fun getWords(categoryRef: String): Single<List<TrainService.Word>> =
        wordsService.getWordsForCategory(categoryRef)
            .map {
                it.map {
                    TrainService.Word(
                        ref = it.ref,
                        name = it.name
                    )
                }
            }

    override fun startWordTranslation(categoryRef: String): Completable =
        getWords(categoryRef)
            .map {
                wordsToLearn.clear()
                wordsToLearn.addAll(it)
                createPages()
                mode = Mode.WORD_TRANSLATION
            }
            .toCompletable()

    override fun startTranslationWord(categoryRef: String): Completable =
        getWords(categoryRef)
            .map {
                wordsToLearn.clear()
                wordsToLearn.addAll(it)
                createPages()
                mode = Mode.TRANSLATION_WORD
            }
            .toCompletable()

    private fun createPages() {
        pages.clear()
        var accumulator = 0
        var currentPage = ArrayList<Int>()
        pages.add(currentPage)

        wordsToLearn.forEachIndexed { index, word ->
            currentPage.add(index)
            accumulator += 1

            if (accumulator >= PAGE_SIZE && wordsToLearn.size != index + 1) {
                accumulator = 0
                currentPage = ArrayList()
                pages.add(currentPage)
            }
        }

        currentPageIndex = 0
    }

    override fun currentWord(): Single<TrainService.Word> =
        Single.fromCallable {
            val page = pages[currentPageIndex]
            val wordPos = page[currentWordIndexRelativePage]
            wordsToLearn[wordPos]
        }

    override fun nextWord(): Single<TrainService.Word> =
        Single.fromCallable {
            currentWordIndexRelativePage += 1
            val page = pages[currentPageIndex]

            if (currentWordIndexRelativePage >= page.size) {
                nextPage()
            }
        }
            .flatMap { currentWord() }

    private fun nextPage() {
        currentPageIndex += 1

        if (currentPageIndex >= pages.size) {
            currentPageIndex = 0
            finishEvents.onNext(true)
            return
        }

        currentWordIndexRelativePage = 0
        newPageEventsSubject.onNext(true)
    }

    override fun right(): Single<TrainService.WordStatus> =
        Single.just(
            TrainService.WordStatus(
                pos = currentWordIndexRelativePage,
                isRight = true
            )
        )

    override fun wrong(): Single<TrainService.WordStatus> =
        Single.just(
            TrainService.WordStatus(
                pos = currentWordIndexRelativePage,
                isRight = false
            )
        )

    override fun reveal(): Single<WordsService.WordDetails> =
        currentWord().flatMap {
            wordsService.getWordDetails(it.ref)
        }

    override fun newPageEvents(): Subject<Boolean> = newPageEventsSubject

    override fun finishEvents(): Subject<Boolean> = finishEvents
}
