package net.rubygrapefruit.cli.parser

internal interface StringTypeInternal<out T> : StringType<T> {
    fun convert(value: String): Result<T>

    sealed interface Result<out T>

    class Success<T>(val value: T) : Result<T>
    class Failure(val problem: Problem) : Result<Nothing>
}