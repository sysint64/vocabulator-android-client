package ru.kabylin.andrey.vocabulator.client.http

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.intellij.lang.annotations.Language
import ru.kabylin.andrey.vocabulator.services.DataStorage
import com.google.gson.JsonParser
import ru.kabylin.andrey.vocabulator.client.LogicError

/**
 * Преобразует ошибки пришедшие от сервера в понятные приложению.
 */
class ErrorsConverter(val dataStorage: DataStorage) {
    fun parseResponse(@Language("Json") json: String): HashMap<String, Any> {
        val gson = Gson()
        var error = HashMap<String, Any>()
        error = gson.fromJson(json, error.javaClass)
        return error
    }

    fun convert(@Language("Json") json: String): Throwable {
        val parser = JsonParser()
        return convert(parser.parse(json).asJsonObject)
    }

    @Suppress("UNCHECKED_CAST")
    private fun convert(response: JsonObject): Throwable {
        val code = response["code"].asInt
        return LogicError(response["info"].asString)
    }
}
