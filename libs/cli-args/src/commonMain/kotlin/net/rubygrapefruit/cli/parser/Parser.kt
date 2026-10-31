package net.rubygrapefruit.cli.parser

/**
 * Parses a list of arguments to produce a result of type [T].
 */
sealed interface Parser<out T> {
    /**
     * Parse the given arguments.
     */
    fun parse(args: List<String>): Result<T>

    /**
     * Parse the given arguments.
     */
    fun parse(vararg args: String): Result<T> {
        return parse(args.toList())
    }

    sealed interface Result<out T> {
        /**
         * Return the result or throw an exception.
         */
        fun get(): T
    }

    object Failure : Result<Nothing> {
        override fun get(): Nothing {
            throw IllegalStateException()
        }
    }

    class Success<T>(val result: T) : Result<T> {
        override fun get(): T {
            return result
        }
    }
}