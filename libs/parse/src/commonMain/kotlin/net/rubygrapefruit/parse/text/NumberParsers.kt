package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.*

/**
 * Returns a parser that matches a positive integer
 */
fun integer(): Parser<TextInput, Int> {
    val zero = literal("0", 0)
    val first = discard(oneInRange('1'..'9'))
    val subsequent = discard(oneInRange('0'..'9'))
    val nonZero = map(match(sequence(first, zeroOrMore(subsequent)))) { it.toInt() }
    return describedAs(oneOf(zero, nonZero), "an integer")
}
