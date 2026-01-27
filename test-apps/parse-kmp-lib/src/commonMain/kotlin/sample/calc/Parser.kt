package sample.calc

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.text.*

class Parser {
    fun parse(text: String): ParseResult<*, List<Expression>> {
        val digit = oneOf('0'..'9')

        val whitespace = zeroOrMore(literal(" "))

        val digits = sequence(digit, zeroOrMore(digit)) { _, _ -> }
        val number = map(match(digits)) { Number(it.toInt()) }

        val expression = recursive<CharInput, Expression>()

        val parenExpression = sequence(literal("("), expression, literal(")")) { _, b, _ -> b }
        val operand = oneOf(number, parenExpression)

        val plus = sequence(whitespace, literal("+"), whitespace) { _, _, _ -> }
        val minus = sequence(whitespace, literal("-"), whitespace) { _, _, _ -> }

        val addition = sequence(operand, plus, operand) { a, _, b -> Addition(a, b) }
        val subtraction = sequence(operand, minus, operand) { a, _, b -> Subtraction(a, b) }

        expression.parser(oneOf(addition, subtraction, operand))

        val statement = sequence(whitespace, expression, whitespace) { _, b, _ -> b }

        // expression = operand ("+" operand | "-" operand)*
        // operand = number | "(" expression ")"
        //
        // plus = expression "+" operand
        // minus = expression "-" operand
        // operand = number | "(" expression ")"
        // expression = plus | minus | operand

        val separator = oneOf(',', '\n')
        val blankLine = sequence(whitespace, literal("\n")) { _, _ -> }
        val parser = sequence(statement, zeroOrMore(prefixed(separator, statement)), zeroOrMore(blankLine)) { a, b, _ -> listOf(a) + b }

        return parser.parse(text)
    }
}