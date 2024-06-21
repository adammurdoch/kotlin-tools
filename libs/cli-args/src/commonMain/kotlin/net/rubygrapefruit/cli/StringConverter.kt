package net.rubygrapefruit.cli

import kotlin.reflect.KClass

internal interface StringConverter<T : Any> {
    val type: KClass<T>

    /**
     * Empty if this converter cannot list the candidates.
     */
    val candidateValues: List<String>
        get() = emptyList()

    fun convert(displayName: String, value: String): Result<T>
}

internal object IntConverter : StringConverter<Int> {
    override val type: KClass<Int>
        get() = Int::class

    override fun convert(displayName: String, value: String): Result<Int> {
        val converted = value.toIntOrNull()
        return if (converted == null) {
            Result.failure(ArgParseException("Value for $displayName is not an integer: $value"))
        } else {
            Result.success(converted)
        }
    }
}

internal object BooleanConverter : StringConverter<Boolean> {
    override val type: KClass<Boolean>
        get() = Boolean::class

    override val candidateValues: List<String>
        get() = listOf("yes", "no")

    override fun convert(displayName: String, value: String): Result<Boolean> {
        return when (value) {
            "yes" -> Result.success(true)
            "no" -> Result.success(false)
            else -> Result.failure(ArgParseException("Unknown value for $displayName: $value"))
        }
    }
}

internal object NoOpConverter : StringConverter<String> {
    override val type: KClass<String>
        get() = String::class

    override fun convert(displayName: String, value: String): Result<String> {
        return Result.success(value)
    }
}

internal class ChoiceConverter<T : Any>(override val type: KClass<T>, val choices: Map<String, ChoiceDetails<T>>) : StringConverter<T> {
    override val candidateValues: List<String>
        get() = choices.keys.toList()

    override fun convert(displayName: String, value: String): Result<T> {
        val item = choices[value]
        return if (item == null) {
            Result.failure(ArgParseException("Unknown value for $displayName: $value"))
        } else {
            Result.success(item.value)
        }
    }
}

internal class MappingConverter<T : Any>(override val type: KClass<T>, val converter: (String) -> Action.ConversionResult<T>) : StringConverter<T> {
    override fun convert(displayName: String, value: String): Result<T> {
        val result = converter(value)
        return when (result) {
            is Action.ConversionResult.Failure -> Result.failure(ArgParseException("Value for $displayName ${result.problem}: $value"))
            is Action.ConversionResult.Success -> Result.success(result.value)
        }
    }
}