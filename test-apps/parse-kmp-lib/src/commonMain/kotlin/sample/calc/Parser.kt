package sample.calc

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.oneOf
import net.rubygrapefruit.parse.combinators.prefixed
import net.rubygrapefruit.parse.combinators.sequence
import net.rubygrapefruit.parse.combinators.zeroOrMore
import net.rubygrapefruit.parse.text.literal
import net.rubygrapefruit.parse.text.parse

class Parser {
    fun parse(text: String): ParseResult<*, List<Expression>> {
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

        val whitespace = zeroOrMore(literal(" "))

        val plus = sequence(whitespace, literal("+"), whitespace) { _, _, _ -> }
        val minus = sequence(whitespace, literal("-"), whitespace) { _, _, _ -> }

        val number = sequence(digit, zeroOrMore(digit)) { a, b -> Number(("$a" + b.joinToString("")).toInt()) }
        val addition = sequence(number, plus, number) { a, _, b -> Addition(a, b) }
        val subtraction = sequence(number, minus, number) { a, _, b -> Subtraction(a, b) }
        val expression = oneOf(addition, subtraction, number)

        val statement = sequence(whitespace, expression, whitespace) { _, b, _ -> b }

        val separator = oneOf(
            literal(","),
            literal("\n")
        )
        val blankLine = zeroOrMore(sequence(whitespace, literal("\n")) { _, _ -> })
        val parser = sequence(statement, zeroOrMore(prefixed(separator, statement)), zeroOrMore(blankLine)) { a, b, _ -> listOf(a) + b }

        return parser.parse(text)
    }
}