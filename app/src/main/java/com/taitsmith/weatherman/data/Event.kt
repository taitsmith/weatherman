package com.taitsmith.weatherman.data

/**
 * https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 */
open class Event<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        }
        else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content
}