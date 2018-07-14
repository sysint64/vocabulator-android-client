package ru.kabylin.andrey.vocabulator

import android.graphics.Color

fun getScoreColor(baseColor: Int, maxColor: Int, score: Int): Int {
    if (score == 0)
        return Color.rgb(maxColor, maxColor, maxColor)

    val normalizedScore = score / 10.0f
    val green = (maxColor - baseColor) * normalizedScore
    val red = (maxColor - baseColor) * (1.0f - normalizedScore)

    return Color.rgb(baseColor + red.toInt(), baseColor + green.toInt(), baseColor)
}