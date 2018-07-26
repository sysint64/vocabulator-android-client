package ru.kabylin.andrey.vocabulator.ui

import android.app.Activity
import android.view.View
import org.junit.Assert
import org.robolectric.Robolectric
import org.robolectric.Shadows

fun viewShouldBeVisible(view: View) = Assert.assertEquals(View.VISIBLE, view.visibility)

fun viewShouldBeHidden(view: View) = assert(view.visibility == View.GONE || view.visibility == View.INVISIBLE)

fun activityShouldBeFinished(activity: Activity) =
    Assert.assertEquals(true, Shadows.shadowOf(activity).isFinishing)

fun activityShouldNotBeFinished(activity: Activity) =
    Assert.assertEquals(false, Shadows.shadowOf(activity).isFinishing)

fun activityShouldReturnResultOk(activity: Activity) =
    Assert.assertEquals(Activity.RESULT_OK, Shadows.shadowOf(activity).resultCode)

fun <T : Activity> shouldOpenActivity(activity: Activity, cls: Class<T>): T {
    val startedIntent = Shadows.shadowOf(activity).nextStartedActivity

    if (startedIntent == null) {
        println()
        println("Expected: move from ${activity::class.java.name} to ${cls.name}")
        println("Actual: move from ${activity::class.java.name} to null")
        println()

        throw AssertionError("equal error")
    }

    val shadowIntent = Shadows.shadowOf(startedIntent)

    if (cls != shadowIntent.intentClass) {
        println()
        println("Expected: move from ${activity::class.java.name} to ${cls.name}}")
        println("Actual: move from ${activity::class.java.name} to ${shadowIntent.intentClass.name}}")
        println()

        throw AssertionError("equal error")
    }

    return Robolectric.buildActivity(cls, startedIntent).create().resume().get()
}
