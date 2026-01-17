package sample.calc

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.parse

class Parser {
    fun parse(args: List<String>): ParseResult<*, Expression> {
        val combined = args.joinToString(" ")

        val digit = oneOf(
            literal("0", 0),
            literal("1", 1),
            literal("2", 2),
            literal("3", 3),
            literal("4", 4),
            literal("5", 5),
            literal("6", 6),
            literal("7", 7),
            literal("8", 8),
            literal("9", 9)
        )
        val addition = sequence(digit, sequence(literal("+"), digit) { _, d -> d }) { a, b ->
            Addition(Number(a), Number(b))
        }
        val subtraction = sequence(digit, sequence(literal("-"), digit) { _, d -> d }) { a, b ->
            Subtraction(Number(a), Number(b))
        }
        val parser = oneOf(addition, subtraction)
        return parser.parse(combined)
    }
}