package net.rubygrapefruit.cli.parser

internal interface StringTypeInternal<out T> : StringType<T> {
    /**
     * A description of the expected values that this type accepts
     */
    val expecting: List<String>

    fun convert(value: String): StringType.Result<T>
}