package net.rubygrapefruit.cli

internal interface Matcher<T : Any> {
    fun match(args: List<String>): Result<T>

    sealed class Result<out T> {
        abstract val consumed: Int
    }

    class Nothing<T> : Result<T>() {
        override val consumed: Int
            get() = 0
    }

    data class Success<T>(override val consumed: Int, val value: T) : Result<T>()
    data class Failure<T>(override val consumed: Int, val failure: ArgParseException, val expectedMore: Boolean) : Result<T>()
}