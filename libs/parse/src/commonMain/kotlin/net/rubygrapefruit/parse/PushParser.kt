package net.rubygrapefruit.parse

interface PushParser<out POS, out OUT> {
    /**
     * Signals that the end of the input has been reached.
     * Returns the parse result.
     */
    fun endOfInput(): ParseResult<POS, OUT>
}