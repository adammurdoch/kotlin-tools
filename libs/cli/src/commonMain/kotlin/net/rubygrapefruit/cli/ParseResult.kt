package net.rubygrapefruit.cli

internal class ParseResult(val count: Int, val failure: ArgParseException?) {
    companion object {
        internal val Nothing = ParseResult(0, null)
        internal val One = ParseResult(1, null)
    }

    fun advance(count: Int) = ParseResult(this.count + count, this.failure)
}
