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

    sealed class Result<T : Any>

    data class Success<T : Any>(val value: T) : Result<T>()

    data class Failure<T : Any>(val message: String) : Result<T>()
}

internal object IntConverter : StringConverter<Int> {
    override val type: KClass<Int>
        get() = Int::class

    override fun convert(displayName: String, value: String): StringConverter.Result<Int> {
        val converted = value.toIntOrNull()
        return if (converted == null) {
            StringConverter.Failure("Value for $displayName is not an integer: $value")
        } else {
            StringConverter.Success(converted)
        }
    }
}

internal object BooleanConverter : StringConverter<Boolean> {
    override val type: KClass<Boolean>
        get() = Boolean::class

    override val candidateValues: List<String>
        get() = listOf("yes", "no")

    override fun convert(displayName: String, value: String): StringConverter.Result<Boolean> {
        return when (value) {
            "yes" -> StringConverter.Success(true)
            "no" -> StringConverter.Success(false)
            else -> StringConverter.Failure("Unknown value for $displayName: $value")
        }
    }
}

internal object IdentityConverter : StringConverter<String> {
    override val type: KClass<String>
        get() = String::class

    override fun convert(displayName: String, value: String): StringConverter.Result<String> {
        return StringConverter.Success(value)
    }
}

internal class ChoiceConverter<T : Any>(override val type: KClass<T>, val choices: Map<String, ChoiceDetails<T>>) : StringConverter<T> {
    override val candidateValues: List<String>
        get() = choices.keys.toList()

    override fun convert(displayName: String, value: String): StringConverter.Result<T> {
        val item = choices[value]
        return if (item == null) {
            StringConverter.Failure("Unknown value for $displayName: $value")
        } else {
            StringConverter.Success(item.value)
        }
    }
}

internal class MappingConverter<T : Any>(override val type: KClass<T>, val converter: (String) -> Action.ConversionResult<T>) : StringConverter<T> {
    override fun convert(displayName: String, value: String): StringConverter.Result<T> {
        val result = try {
            converter(value)
        } catch (e: Throwable) {
            throw ArgParseException("Could not convert value for $displayName: $value", cause = e)
        }
        return when (result) {
            is Action.ConversionResult.Failure -> StringConverter.Failure("Value for $displayName ${result.problem}: $value")
            is Action.ConversionResult.Success -> StringConverter.Success(result.value)
        }
    }
}