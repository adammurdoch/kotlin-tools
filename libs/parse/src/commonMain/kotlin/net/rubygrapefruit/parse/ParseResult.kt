package net.rubygrapefruit.parse

sealed interface ParseResult<out CONTEXT, out OUT> {
    /**
     * Returns the parse result or throws an exception if parse failed.
     */
    @Throws(ParseException::class)
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
    class Fail<CONTEXT> internal constructor(val context: CONTEXT, val message: String, val formatter: (CONTEXT, String) -> String) : ParseResult<CONTEXT, Nothing> {
        override fun get(): Nothing {
            throw ParseException(formatter(context, message))
        }
    }
}