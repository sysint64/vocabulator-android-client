package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class RealTrainService(
    val wordsService: WordsService,
    val scoreService: ScoreService
) : TrainService {

    companion object {
        const val PAGE_SIZE = 10
    }

    private var mode = TrainService.Mode.REVISION
    private var wordTitle = WordsService.Title.WORD
    private var wordsToLearn = ArrayList<TrainService.Word>()

    data class WordInPage(
        val indexInPage: Int,
        val isRight: Boolean,
        val wordIndex: Int
    )

    private var pages = ArrayList<ArrayList<WordInPage>>()
    private var currentPageIndex: Int = 0
    private val newPageEventsSubject = PublishSubject.create<Boolean>()
    private val finishEvents = PublishSubject.create<Boolean>()
    private var currentWordIndexRelativePage = 0

    private fun getOrderBy(): WordsService.OrderBy =
        when (mode) {
            TrainService.Mode.REVISION -> WordsService.OrderBy.REVISION_MODE
            TrainService.Mode.LEARNING -> WordsService.OrderBy.LEARNING_MODE
            TrainService.Mode.RANDOM -> WordsService.OrderBy.RANDOM
        }

    override fun setWordTitleForLanguage(languageRef: String, title: WordsService.Title): Completable =
        Completable.fromAction {
            wordTitle = title
        }

    override fun startByModeForCategory(categoryRef: String, mode: TrainService.Mode): Completable {
        this.mode = mode

        return wordsService.getWordsForCategory(categoryRef, wordTitle, getOrderBy())
            .map {
                it.map {
                    TrainService.Word(
                        ref = it.ref,
                        name = it.name
                    )
                }
            }
            .map {
                init(it)
            }
            .toCompletable()
    }

    override fun startByModeForLanguage(languageRef: String, mode: TrainService.Mode): Completable {
        this.mode = mode

        return wordsService.getWordsForLanguage(languageRef, wordTitle, getOrderBy())
            .map {
                it.map {
                    TrainService.Word(
                        ref = it.ref,
                        name = it.name
                    )
                }
            }
            .map {
                init(it)
            }
            .toCompletable()
    }

    private fun init(words: List<TrainService.Word>) {
        wordsToLearn.clear()
        wordsToLearn.addAll(words)
        createPages()

        currentWordIndexRelativePage = 0
        currentPageIndex = 0
    }

    private fun createPages() {
        pages.clear()
        var accumulator = 0
        var currentPage = ArrayList<WordInPage>()
        pages.add(currentPage)

        wordsToLearn.forEachIndexed { index, _ ->
            currentPage.add(WordInPage(accumulator, false, index))
            accumulator += 1

            if (accumulator > PAGE_SIZE && wordsToLearn.size != index + 1) {
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
            val wordPos = page[currentWordIndexRelativePage].wordIndex
            wordsToLearn[wordPos]
        }

    override fun nextWord(): Single<TrainService.Word> =
        Single.fromCallable {
            currentWordIndexRelativePage += 1
            val page = pages[currentPageIndex]

            if (currentWordIndexRelativePage >= page.size) {
                page.removeAll { it.isRight }

                if (page.isEmpty()) {
                    nextPage()
                } else {
                    pages[currentPageIndex].shuffle()
                    currentWordIndexRelativePage = 0
                }
            }
        }
            .flatMap { currentWord() }

    private fun nextPage() {
        currentPageIndex += 1

        if (currentPageIndex >= pages.size) {
            currentPageIndex -= 1
            finishEvents.onNext(true)
            return
        }

        currentWordIndexRelativePage = 0
        newPageEventsSubject.onNext(true)
    }

    override fun right(): Single<TrainService.WordStatus> {
        val page = pages[currentPageIndex]
        val word = page[currentWordIndexRelativePage]

        return scoreService.rightWord(mode, wordsToLearn[word.wordIndex].ref)
            .toSingle {
                page[currentWordIndexRelativePage] = word.copy(isRight = true)

                TrainService.WordStatus(
                    pos = word.indexInPage,
                    isRight = true
                )
            }
    }

    override fun wrong(): Single<TrainService.WordStatus> {
        val page = pages[currentPageIndex]
        val word = page[currentWordIndexRelativePage]

        return scoreService.wrongWord(mode, wordsToLearn[word.wordIndex].ref)
            .toSingle {
                TrainService.WordStatus(
                    pos = word.indexInPage,
                    isRight = false
                )
            }
    }

    override fun reveal(): Single<WordsService.WordDetails> =
        currentWord().flatMap {
            wordsService.getWordDetails(
                it.ref,
                addWordTitle = wordTitle == WordsService.Title.TRANSLATION_OR_DEFINITION
            )
        }

    override fun newPageEvents(): Subject<Boolean> = newPageEventsSubject

    override fun finishEvents(): Subject<Boolean> = finishEvents
}
