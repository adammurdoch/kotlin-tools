package net.rubygrapefruit.cli

internal sealed class ParseResult {
    // Number of arguments recognized, including badly formed/unexpected argument referenced by the exception
    abstract val recognized: Int

    abstract val failure: ArgParseException?

    abstract fun prepend(count: Int): ParseResult

    abstract fun asFinishResult(): FinishResult

    companion object {
        fun of(count: Int, failure: FinishResult.Failure?): ParseResult {
            return if (failure != null) {
                Failure(count, failure.failure, failure.expectedMore)
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

    /**
     * Did not recognize anything.
     */
    data object Nothing : ParseResult() {
        override val recognized: Int
            get() = 0

        override val failure: ArgParseException?
            get() = null

        override fun prepend(count: Int): Success {
            return Success(count)
        }

        override fun asFinishResult(): FinishResult {
            return FinishResult.Success
        }
    }

    /**
     * Succeeded, possibly by consuming nothing.
     */
    data class Success(override val recognized: Int) : ParseResult() {
        override val failure: ArgParseException?
            get() = null

        override fun prepend(count: Int): Success {
            return Success(this.recognized + count)
        }

        override fun asFinishResult(): FinishResult {
            return FinishResult.Success
        }
    }

    /**
     * Failed, possibly by consuming nothing.
     */
    data class Failure(override val recognized: Int, override val failure: ArgParseException, val expectedMore: Boolean = false) : ParseResult() {
        override fun prepend(count: Int): Failure {
            return Failure(this.recognized + count, this.failure, this.expectedMore)
        }

        override fun asFinishResult(): FinishResult.Failure {
            return FinishResult.Failure(failure, expectedMore)
        }
    }
}
