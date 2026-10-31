package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.ArgParseException

internal interface ParserInternal<out T> {
    fun maybeParse(args: InputArgs): Result<T>

    sealed interface Result<out T>

    data class Success<T>(val count: Int, override val result: T) : Result<T>, Parser.Success<T> {
        override fun get(): T {
            return result
        }
    }

    object NotRecognized : Result<Nothing>

    data class Failure(override val problems: List<Parser.Problem>) : Result<Nothing>, Parser.Failure {
        constructor(index: Int, message: String) : this(listOf(Parser.Problem(index, message)))

        override fun get(): Nothing {
            throw ArgParseException("Argument parsing failed")
        }
    }
}