package net.rubygrapefruit.parse

internal interface FailureContext<POS> {
    val pos: POS

    fun formattedMessage(expected: String): String
}