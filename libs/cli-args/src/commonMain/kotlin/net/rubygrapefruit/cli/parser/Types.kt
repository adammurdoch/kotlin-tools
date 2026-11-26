package net.rubygrapefruit.cli.parser

/**
 * Produces unsigned integers from string arguments.
 */
fun unsignedInteger(): StringType<Int> {
    return UnsignedIntType
}

/**
 * Produces values of type [T] by matching arguments against fixed strings. Matching is case-sensitive
 */
fun <T> oneOf(vararg mapping: Pair<String, T>): StringType<T> {
    return OneOfType(mapping.toMap())
}

/**
 * Produces values of type [T] from arguments by calling the given function.
 */
fun <T> map(summary: String, mapping: (String) -> StringType.Result<T>): StringType<T> {
    return MappingType(summary, mapping)
}