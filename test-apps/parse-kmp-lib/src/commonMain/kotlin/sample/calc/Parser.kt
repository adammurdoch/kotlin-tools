package sample.calc

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.oneOf
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
        val number = sequence(digit, zeroOrMore(digit)) { a, b -> Number(("$a" + b.joinToString("")).toInt()) }
        val addition = sequence(number, sequence(literal("+"), number) { _, d -> d }) { a, b ->
            Addition(a, b)
        }
        val subtraction = sequence(number, sequence(literal("-"), number) { _, d -> d }) { a, b ->
            Subtraction(a, b)
        }
        val expression = oneOf(addition, subtraction, number)
        val line = sequence(expression, literal("\n")) { a, _ -> a }
        val parser = sequence(zeroOrMore(line), zeroOrMore(expression)) { a, b -> a + b }
        return parser.parse(text)
    }
}