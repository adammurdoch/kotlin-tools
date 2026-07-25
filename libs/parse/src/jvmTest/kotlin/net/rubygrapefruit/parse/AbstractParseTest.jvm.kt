package net.rubygrapefruit.parse

actual fun Throwable.fixStackTrace() {
    fillInStackTrace()
}