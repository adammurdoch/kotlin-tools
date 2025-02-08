package net.rubygrapefruit.cli

/**
 * Matches one or more arguments to produce a value of type [T].
 */
internal interface Matcher<T : Any> {
    val markers: List<String>

    /**
     * Attempts to match the given arguments.
     */
    fun match(args: List<String>): Result<T>

    fun usage(): List<NonPositionalUsage>

    sealed class Result<out T> {
        abstract val consumed: Int
    }

    class Nothing<T> : Result<T>() {
        override val consumed: Int
            get() = 0
    }

    data class Success<T>(override val consumed: Int, val value: T) : Result<T>()

    data class Failure<T>(override val consumed: Int, val message: String, val expectedMore: Boolean) : Result<T>() {
        fun toParseState() = ParseState.Failure(consumed, message, expectedMore = expectedMore)
    }
}