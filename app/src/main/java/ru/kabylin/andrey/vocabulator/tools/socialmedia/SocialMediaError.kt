package ru.kabylin.andrey.vocabulator.tools.socialmedia

object CancelReason

class SocialMediaError(message: String? = null, val reason: Any? = null)
    : RuntimeException(message)
