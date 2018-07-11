package ru.kabylin.andrey.vocabulator.ext

import java.text.DateFormat
import java.text.DateFormat.*
import java.text.SimpleDateFormat
import java.util.*

fun nowTime(): Long = now().time

fun now(): Date = Calendar.getInstance().time

val Date.calendar: Calendar
    get() {
        val calendar = Calendar.getInstance()
        calendar.time = this
        return calendar
    }

fun Date.add(value: Int = 0, units: Int = Calendar.DAY_OF_MONTH): Date {
    val calendar = this.calendar
    calendar.add(units, value)
    return calendar.time
}


fun createDateTime(date: Date, time: Date): Date {
    with(Calendar.getInstance()) {
        set(Calendar.YEAR, date.calendar.get(Calendar.YEAR))
        set(Calendar.MONTH, date.calendar.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, date.calendar.get(Calendar.DAY_OF_MONTH))

        set(Calendar.HOUR_OF_DAY, time.calendar.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, time.calendar.get(Calendar.MINUTE))
        set(Calendar.SECOND, time.calendar.get(Calendar.SECOND))
        set(Calendar.MILLISECOND, time.calendar.get(Calendar.MILLISECOND))

        return this.time
    }
}

fun createDateTime(year: Int, month: Int, day: Int, hours: Int, minutes: Int, seconds: Int): Date =
    createDateTime(createDate(year, month, day), createTime(hours, minutes, seconds))

fun createDate(year: Int, month: Int, day: Int): Date =
    with(Calendar.getInstance()) {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month - 1) // the month between 0-11
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        return time
    }

fun createTime(hours: Int, minutes: Int, seconds: Int): Date {
    with(Calendar.getInstance()) {
        set(Calendar.HOUR_OF_DAY, hours)
        set(Calendar.MINUTE, minutes)
        set(Calendar.SECOND, seconds)

        return time
    }
}

enum class DateFormats(val pattern: String? = null) {
    SIMPLE_DATE("dd.MM.yyyy"),

    ISO_8601_DATE("yyyy-MM-dd"),

    ISO_8601_DATE_TIME("yyyy-MM-dd'T'HH:mm:ss"),

    TIME("HH:mm"),

    HUMANIZE_DATE,

    HUMANIZE_DATE_FULL,

    HUMANIZE_DATE_AND_TIME,

    HUMANIZE_DATE_AND_TIME_FULL,
}

fun createDate(text: String, pattern: String): Date =
    SimpleDateFormat(pattern, Locale.US).parse(text)

fun createDate(text: String, format: DateFormats): Date =
    when (format) {
        DateFormats.HUMANIZE_DATE -> DateFormat.getDateInstance().parse(text)
        DateFormats.HUMANIZE_DATE_AND_TIME -> DateFormat.getDateTimeInstance().parse(text)
        else -> createDate(text, format.pattern!!)
    }

fun Date.toString(format: DateFormats): String =
    when (format) {
        DateFormats.HUMANIZE_DATE -> DateFormat.getDateInstance(MEDIUM).format(this)
        DateFormats.HUMANIZE_DATE_FULL -> DateFormat.getDateInstance(FULL).format(this)
        DateFormats.HUMANIZE_DATE_AND_TIME -> DateFormat.getDateTimeInstance(MEDIUM, SHORT).format(this)
        DateFormats.HUMANIZE_DATE_AND_TIME_FULL -> DateFormat.getDateTimeInstance(FULL, SHORT).format(this)
        else -> SimpleDateFormat(format.pattern, Locale.US).format(this)
    }

fun Date.diffTo(date: Date): Int {
    val a = this.calendar
    val b = date.calendar

    var diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR)

    if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE)) {
        diff--
    }

    return diff
}

val Date.age: Int
    get() = this.diffTo(now())
