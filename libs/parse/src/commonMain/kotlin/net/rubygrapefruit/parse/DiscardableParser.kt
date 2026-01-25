package net.rubygrapefruit.parse

internal interface DiscardableParser<in IN> {
    /**
     * Creates a copy of this parser that produces no result.
     */
    fun withNoResult(): Parser<IN, Unit>
}