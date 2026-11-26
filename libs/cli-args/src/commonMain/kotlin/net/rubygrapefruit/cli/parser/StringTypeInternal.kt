package net.rubygrapefruit.cli.parser

internal interface StringTypeInternal<out T> : StringType<T> {
    /**
     * A description of the expected values that this type accepts
     */
    val expecting: List<String>

    fun convert(value: String): Result<T>

    sealed interface Result<out T>

    class Success<T>(val value: T) : Result<T>
    object Failure : Result<Nothing>
}