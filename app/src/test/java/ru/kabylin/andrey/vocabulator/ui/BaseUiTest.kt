package ru.kabylin.andrey.vocabulator.ui

import org.robolectric.RuntimeEnvironment

abstract class BaseUiTest {
    protected val application: UiTestApplication
        get() = RuntimeEnvironment.application as UiTestApplication
}