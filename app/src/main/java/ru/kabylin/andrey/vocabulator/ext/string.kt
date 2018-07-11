package ru.kabylin.andrey.vocabulator.ext

import android.os.Build
import android.telephony.PhoneNumberUtils
import java.util.*

enum class StringFormat {
    PHONE,
    ;
}

@Suppress("deprecation")
fun String.format(format: StringFormat): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        PhoneNumberUtils.formatNumber(this, Locale.getDefault().country)
    } else {
        PhoneNumberUtils.formatNumber(this)
    }
