package net.rubygrapefruit.cli

internal sealed class ParseResult {
    // Number of arguments recognized. Should not include badly formed/unexpected argument carried by the exception
    abstract val count: Int

    abstract val failure: ArgParseException?

    abstract fun consume(count: Int): ParseResult

    companion object {
        fun of(count: Int, failure: ArgParseException?): ParseResult {
            return if (failure != null) {
                Failure(count, failure)
            } else if (count == 0) {
                Nothing
            } else if (count == 1) {
                One
            } else if (count == 2) {
                Two
            } else {
                Success(count)
            }
        }

        internal val Nothing = Success(0)
        internal val One = Success(1)
        internal val Two = Success(2)
    }

    class Success(override val count: Int) : ParseResult() {
        override val failure: ArgParseException?
            get() = null

        override fun consume(count: Int): Success {
            return Success(this.count + count)
        }
    }

    class Failure(override val count: Int, override val failure: ArgParseException, val missing: Boolean = false) : ParseResult() {
        override fun consume(count: Int): Failure {
            return Failure(this.count + count, this.failure, missing)
        }
    }
}
