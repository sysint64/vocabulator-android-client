package ru.kabylin.andrey.vocabulator.views.anim

internal class BounceInterpolator(
    private val amplitude: Double = 1.0,
    private val frequency: Double = 10.0
) : android.view.animation.Interpolator {

    override fun getInterpolation(time: Float): Float {
        return (-1.0 * Math.pow(Math.E, -time / amplitude) *
            Math.cos(frequency * time) + 1).toFloat()
    }
}
