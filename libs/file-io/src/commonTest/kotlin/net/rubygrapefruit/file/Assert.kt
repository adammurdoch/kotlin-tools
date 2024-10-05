package net.rubygrapefruit.file

import kotlin.test.assertEquals
import kotlin.test.fail

inline fun <reified T : Throwable> failsWith(message: String, action: () -> Unit) {
    try {
        action()
        fail("Action should have failed")
    } catch (e: Throwable) {
        if (e !is T) {
            throw e
        }
        assertEquals(message, e.message)
    }
}