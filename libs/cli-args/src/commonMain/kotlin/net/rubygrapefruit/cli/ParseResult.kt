package net.rubygrapefruit.cli

internal data class ParseResult(val count: Int, val failure: ArgParseException?) {
    companion object {
        internal val Nothing = ParseResult(0, null)
        internal val One = ParseResult(1, null)
        internal val Two = ParseResult(2, null)
    }
}
