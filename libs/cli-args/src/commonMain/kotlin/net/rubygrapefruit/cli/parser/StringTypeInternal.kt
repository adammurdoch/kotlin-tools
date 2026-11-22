package net.rubygrapefruit.cli.parser

internal interface StringTypeInternal<out T> : StringType<T> {
    fun convert(value: String): Result<T>

    fun unexpectedType(value: String): Problem?

    sealed interface Result<out T>

    class Success<T>(val value: T) : Result<T>
    object Failure : Result<Nothing>
}