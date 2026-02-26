package net.rubygrapefruit.parse

/**
 * A parser whose input is supplied in batches.
 */
interface PushParser<out CONTEXT, out OUT> {
    /**
     * Signals that the end of the input has been reached.
     * Returns the parse result.
     */
    fun endOfInput(): ParseResult<CONTEXT, OUT>
}