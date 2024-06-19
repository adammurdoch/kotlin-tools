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

internal object FilePathConverter : StringConverter<FilePath> {
    override fun convert(displayName: String, value: String): Result<FilePath> {
        return Result.success(FilePath(value))
    }
}

internal object NoOpConverter : StringConverter<String> {
    override fun convert(displayName: String, value: String): Result<String> {
        return Result.success(value)
    }
}

internal class ChoiceConverter<T>(val choices: Map<String, ChoiceDetails<T>>) : StringConverter<T> {
    override fun convert(displayName: String, value: String): Result<T> {
        val item = choices[value]
        return if (item == null) {
            Result.failure(ArgParseException("Unknown value for $displayName: $value"))
        } else {
            Result.success(item.value)
        }
    }
}