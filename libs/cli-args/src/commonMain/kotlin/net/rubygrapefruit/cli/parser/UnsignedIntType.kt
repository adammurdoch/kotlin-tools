package net.rubygrapefruit.cli.parser

internal object UnsignedIntType : StringTypeInternal<Int> {
    override fun convert(value: String): StringTypeInternal.Result<Int> {
        val int = value.toIntOrNull()
        return if (int == null || int < 0) {
            StringTypeInternal.Failure(ValueIsNotAnInt(value))
        } else {
            StringTypeInternal.Success(int)
        }
    }
}
