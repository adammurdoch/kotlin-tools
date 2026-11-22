package net.rubygrapefruit.cli.parser

internal object UnsignedIntType : StringTypeInternal<Int> {
    override fun unexpectedType(value: String): Problem {
        return ValueIsNotAnInt(value)
    }

    override fun convert(value: String): StringTypeInternal.Result<Int> {
        val int = value.toIntOrNull()
        return if (int == null || int < 0) {
            StringTypeInternal.Failure
        } else {
            StringTypeInternal.Success(int)
        }
    }
}
