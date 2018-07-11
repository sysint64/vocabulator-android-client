package ru.kabylin.andrey.vocabulator.client

import android.content.Context
import android.support.annotation.StringRes
import ru.kabylin.andrey.vocabulator.ContextStringify
import ru.kabylin.andrey.vocabulator.containers.EitherStringRes
import java.io.Serializable

class TransformError(details: String) : RuntimeException(details)

/**
 * Ошибки произошедшие в работе бизнесс логики.
 * пример составления можно найти тут: [ru.kabylin.andrey.vocabulator.client.ErrorsCompositorTest.TestReasons]
 */
abstract class Reason(val payload: Any? = null): ContextStringify

open class DescReason(private val data: EitherStringRes): Reason() {
    override fun toString(context: Context): String = data.toString(context)

    override fun toString(): String = data.toString()

    companion object {
        fun string(string: String): DescReason {
            return DescReason(EitherStringRes.string(string))
        }

        fun res(@StringRes res: Int): DescReason {
            return DescReason(EitherStringRes.res(res))
        }
    }
}

class CanceledError(details: String?) : RuntimeException(details)

/**
 * Логическая ошибка - к примеру, во время регистрации пользователя.
 * ошибка: пользователь с таким именем уже существует, т.е. в данном случае
 * все данные валидны, но зарос все равно не может быть обработан по каким-то причинам,
 * причина должна быть указана в [reason].
 */
class LogicError(val reason: Reason, details: String? = null) : RuntimeException(details) {
    constructor(@StringRes reason: Int): this(DescReason.res(reason))

    constructor(reason: String): this(DescReason.string(reason))
}

// Ошибка валидации данных - к примеру не валидный email, невалидны телефон итд.
class ValidationErrors(val errors: Map<String, String>, details: String? = null) : RuntimeException(details) {
    constructor(field: String, details: String) : this(mapOf(field to details))
}

// Ошибка предоставления прав для пользователя.
class CredentialsError(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

enum class VersionErrorLevel {
    /**
     * Версия вероятно устарела, но приложение все еще может работать коррестно.
     */
    WARNING,
    /**
     * Версия приложения слишком устарела и необходимо обязательно обновиться
     * для корректной работы приложения.
     */
    ERROR
}

@Suppress("ClassName")
sealed class AccessErrorReason(val message: String? = null) : Serializable {
    object NOT_FOUND : AccessErrorReason()
    object LOST_CONNECTION : AccessErrorReason()
    object TOO_MANY_REQUESTS : AccessErrorReason()
    object TIMEOUT : AccessErrorReason()

    // Не удалось распознать ответ от сервер
    object BAD_RESPONSE: AccessErrorReason()

    // Произошла ошибка на сервере
    object INTERNAL_SERVER_ERROR: AccessErrorReason()

    // Старая версия приложения, использующее несовместимое апи
    class VERSION_ERROR(val level: VersionErrorLevel) : AccessErrorReason()

    class UNSPECIFIED(message: String?) : AccessErrorReason(message)
}

/**
 * Проблемы с доступом к ресурсу, к примеру, нет стабильного соединения с сервером,
 * превышен лимит на запросы от пользователя, ресурс не найден итд.
 */
class AccessError(val reason: AccessErrorReason, cause: Throwable? = null) : RuntimeException(reason.message, cause) {
    constructor(message: String, cause: Throwable?) :
        this(AccessErrorReason.UNSPECIFIED(message), cause)

    constructor(cause: Throwable) :
        this(AccessErrorReason.UNSPECIFIED(cause.message), cause)
}

/**
 * Проблемы с сессией, к примеру - не авторизованый запрос.
 */
class SessionError(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)
