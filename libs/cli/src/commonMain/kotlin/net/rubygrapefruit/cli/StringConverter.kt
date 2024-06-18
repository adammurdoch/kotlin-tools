package net.rubygrapefruit.cli

internal interface StringConverter<T> {
    fun convert(displayName: String, value: String): Result<T>
}

internal object IntConverter : StringConverter<Int> {
    override fun convert(displayName: String, value: String): Result<Int> {
        val converted = value.toIntOrNull()
        return if (converted == null) {
            Result.failure(ArgParseException("Value for $displayName is not an integer: $value"))
        } else {
            Result.success(converted)
        }
    }
}

internal object NoOpConverter : StringConverter<String> {
    override fun convert(displayName: String, value: String): Result<String> {
        return Result.success(value)
    }
}