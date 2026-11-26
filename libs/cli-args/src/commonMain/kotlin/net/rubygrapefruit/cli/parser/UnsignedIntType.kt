package net.rubygrapefruit.cli.parser

internal object UnsignedIntType : StringTypeInternal<Int> {
    override val expecting: List<String>
        get() = listOf("an unsigned int")

    override fun convert(value: String): StringType.Result<Int> {
        val int = value.toIntOrNull()
        return if (int == null || int < 0) {
            StringType.Failure
        } else {
            StringType.Success(int)
        }
    }
}
