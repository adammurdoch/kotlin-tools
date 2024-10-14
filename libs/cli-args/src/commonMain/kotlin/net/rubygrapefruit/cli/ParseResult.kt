package net.rubygrapefruit.cli

internal sealed class ParseResult {
    // Number of arguments recognized, including badly formed/unexpected argument referenced by the exception
    abstract val count: Int

    abstract val failure: ArgParseException?

    abstract fun prepend(count: Int): ParseResult

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

        internal val One = Success(1)
        internal val Two = Success(2)
    }

    data object Nothing : ParseResult() {
        override val count: Int
            get() = 0
        override val failure: ArgParseException?
            get() = null

        override fun prepend(count: Int): Success {
            return Success(count)
        }
    }

    class Success(override val count: Int) : ParseResult() {
        override val failure: ArgParseException?
            get() = null

        override fun prepend(count: Int): Success {
            return Success(this.count + count)
        }
    }

    class Failure(override val count: Int, override val failure: ArgParseException, val expectedMore: Boolean = false) : ParseResult() {
        override fun prepend(count: Int): Failure {
            return Failure(this.count + count, this.failure, this.expectedMore)
        }
    }
}
