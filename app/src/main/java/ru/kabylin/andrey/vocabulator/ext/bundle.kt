package ru.kabylin.andrey.vocabulator.ext

import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

fun createBundle(values: Map<String, Any?>): Bundle {
    val bundle = Bundle()

    for ((key, value) in values) {
        when (value) {
            is Int -> bundle.putInt(key, value)
            is String -> bundle.putString(key, value)
            is Double -> bundle.putDouble(key, value)
            is Parcelable -> bundle.putParcelable(key, value)
            is Serializable -> bundle.putSerializable(key, value)
            null -> { /* ignore */ }
            else -> throw UnsupportedOperationException("Unsupported value type ${value::class}")
        }
    }

    return bundle
}
