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
): StringOption {
    val unsortedNames = listOf(name) + otherNames.toList()
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

    return DefaultStringOption(names, summary, valueName)
}