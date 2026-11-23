package net.rubygrapefruit.cli.parser

/**
 * Creates an option with the given name.
 *
 * The returned parser will match `-n <string>` or `--name <string>` or empty input.
 */
fun option(
    name: String,
    vararg otherNames: String,
    summary: String? = null,
    valueName: String = "value"
): ScalarOption<String> {
    return option(listOf(name) + otherNames.toList(), type = IdentityStringType, summary = summary, valueName = valueName)
}

fun <OUT> option(
    name: String,
    type: StringType<OUT>,
    summary: String? = null,
    valueName: String = "value"
): ScalarOption<OUT> {
    return option(listOf(name), type = type, summary = summary, valueName = valueName)
}

fun <OUT> option(
    name: String,
    vararg otherNames: String,
    type: StringType<OUT>,
    summary: String? = null,
    valueName: String = "value"
): ScalarOption<OUT> {
    return option(listOf(name) + otherNames.toList(), type = type, summary = summary, valueName = valueName)
}

private fun <OUT> option(
    unsortedNames: List<String>,
    type: StringType<OUT>,
    summary: String?,
    valueName: String
): ScalarOption<OUT> {
    for (name in unsortedNames) {
        if (name.isEmpty()) {
            throw IllegalArgumentException("Option name cannot be empty")
        }
        if (name.startsWith("-")) {
            throw IllegalArgumentException("Option name '$name' cannot start with '-'")
        }
        if (name.contains(" ")) {
            throw IllegalArgumentException("Option name '$name' cannot contain whitespace")
        }
    }
    val names = unsortedNames.sortedWith { a, b ->
        val diff = a.length.compareTo(b.length)
        if (diff != 0) {
            diff
        } else {
            a.compareTo(b)
        }
    }

    return DefaultOption(names, type as StringTypeInternal, summary, valueName)
}