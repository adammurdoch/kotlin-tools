package net.rubygrapefruit.cli.parser

/**
 * Produces unsigned integers from string arguments.
 */
fun unsignedInteger(): StringType<Int> {
    return UnsignedIntType
}

fun <T> oneOf(vararg mapping: Pair<String, T>): StringType<T> {
    return OneOfType(mapping.toMap())
}