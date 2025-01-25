package net.rubygrapefruit.cli

internal interface Matcher<T : Any> {
    fun match(args: List<String>): Result<T>

    fun accepts(option: String): Boolean

    sealed class Result<out T> {
        abstract val consumed: Int

        abstract fun asParseResult(): ParseResult
    }

    class Nothing<T> : Result<T>() {
        override val consumed: Int
            get() = 0

        override fun asParseResult(): ParseResult = ParseResult.Nothing
    }

    data class Success<T>(override val consumed: Int, val value: T) : Result<T>() {
        override fun asParseResult(): ParseResult {
            return ParseResult.Success(consumed)
        }
    }

    data class Failure<T>(override val consumed: Int, val message: String, val expectedMore: Boolean) : Result<T>() {
        override fun asParseResult(): ParseResult {
            return ParseResult.Failure(consumed, ArgParseException(message), expectedMore)
        }
    }
}