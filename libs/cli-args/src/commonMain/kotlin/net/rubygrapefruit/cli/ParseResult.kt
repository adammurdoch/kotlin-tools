package net.rubygrapefruit.cli

internal data class ParseResult(val count: Int, val failure: ArgParseException?, val finished: Boolean) {
    companion object {
        internal val Nothing = ParseResult(0, null, false)
        internal val One = ParseResult(1, null, true)
        internal val Two = ParseResult(2, null, true)
    }
}
