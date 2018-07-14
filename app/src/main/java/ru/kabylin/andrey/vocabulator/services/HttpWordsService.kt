package ru.kabylin.andrey.vocabulator.services

import io.reactivex.Single
import ru.kabylin.andrey.vocabulator.client.http.HttpClient

class HttpWordsService(client: HttpClient) : WordsService {
    private val categories = listOf(
        WordsService.Category(
            ref = "1",
            image = "https://images.unsplash.com/photo-1489065094455-c2d576ff27a0?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=eb24765f872afe8f0daf28dec236b745&w=1000&q=80",
            name = "Phrasal verbs"
        ),
        WordsService.Category(
            ref = "2",
            image = null,
            name = "Category 2"
        ),
        WordsService.Category(
            ref = "3",
            image = null,
            name = "Category 3"
        )
    )

    private val words = listOf(
        Pair("1", WordsService.Word(ref = "1", name = "look up to", score = 1)),
        Pair("1", WordsService.Word(ref = "1", name = "put out", score = 1)),
        Pair("1", WordsService.Word(ref = "2", name = "take out", score = 3)),
        Pair("1", WordsService.Word(ref = "3", name = "look forward", score = 8)),
        Pair("1", WordsService.Word(ref = "4", name = "give up", score = 10)),
        Pair("2", WordsService.Word(ref = "5", name = "descent", score = 8)),
        Pair("2", WordsService.Word(ref = "6", name = "category", score = 9)),
        Pair("2", WordsService.Word(ref = "7", name = "dog", score = 10)),
        Pair("2", WordsService.Word(ref = "8", name = "cat", score = 10)),
        Pair("3", WordsService.Word(ref = "9", name = "square", score = 1)),
        Pair("3", WordsService.Word(ref = "10", name = "circle", score = 4)),
        Pair("3", WordsService.Word(ref = "11", name = "triangle", score = 4)),
        Pair("3", WordsService.Word(ref = "12", name = "sphere", score = 4)),
        Pair("3", WordsService.Word(ref = "13", name = "cube", score = 4)),
        Pair("3", WordsService.Word(ref = "14", name = "tetrameter", score = 7)),
        Pair("3", WordsService.Word(ref = "16", name = "temperament", score = 0))
    )

    override fun getCategories(): Single<List<WordsService.Category>> =
        Single.just(categories)

    override fun getWordsForCategory(categoryRef: String): Single<List<WordsService.Word>> =
        Single.just(
            words
                .filter { it.first == categoryRef }
                .map { it.second }
        )

    override fun getScoresCounts(categoryRef: String): Single<List<WordsService.CategoryScore>> =
        getWordsForCategory(categoryRef)
            .map { words ->
                (0..10).map { score ->
                    WordsService.CategoryScore(
                        score = score,
                        count = words.count { it.score == score }
                    )
                }
            }

    override fun getWordDetails(ref: String): Single<WordsService.WordDetails> =
        Single.just(
            WordsService.WordDetails(
                ref = ref,
                name = words.first { it.second.ref == ref }.second.name,
                details = listOf(
                    WordsService.TitleValue(
                        title = "Pronounce",
                        value = "ˈbrākˌTHro͞o"
                    )
                ),
                translations = listOf(
                    "прорвать",
                    "прорыв",
                    "достижение"
                ),
                definitions = listOf(
                    WordsService.Definition(
                        title = "noun",
                        desc = "a sudden, dramatic, and important discovery or development.",
                        example = "a major breakthrough in DNA research",
                        synonyms = "advance, development, step forward, success, improvement, discovery, innovation, revolution, progress, headway".split(",")
                    ),
                    WordsService.Definition(
                        title = "noun",
                        desc = "a sudden, dramatic, and important discovery or development.",
                        example = "",
                        synonyms = "advance, development".split(",")
                    ),
                    WordsService.Definition(
                        title = "noun",
                        desc = "a sudden, dramatic, and important discovery or development.",
                        example = "a major breakthrough in DNA research",
                        synonyms = listOf()
                    )
                )
            )
        )
}
