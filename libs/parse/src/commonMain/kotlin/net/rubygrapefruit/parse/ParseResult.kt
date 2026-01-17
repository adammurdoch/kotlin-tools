package net.rubygrapefruit.parse

sealed interface ParseResult<out POS, out OUT> {
    /**
     * Returns the parse result or throws an exception if parse failed.
     */
    fun get(): OUT

    /**
     * Parsing succeeded with the given result.
     */
    class Success<OUT> internal constructor(val value: OUT) : ParseResult<Nothing, OUT> {
        override fun get(): OUT {
            return value
        }
    }

    /**
     * Parsing failed with the given failure.
     */
    class Fail<POS> internal constructor(private val context: FailureContext<POS>, val message: String) : ParseResult<POS, Nothing> {
        val position: POS
            get() = context.pos

        override fun get(): Nothing {
            throw IllegalStateException(context.formattedMessage(message))
        }
    }
}