package sample.calc

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.text.*

class Parser {
    fun parse(text: String): ParseResult<*, List<Expression>> {
        val whitespace = zeroOrMore(literal(" "))

        val digit = describedAs(oneInRange('0'..'9'), "a digit")
        val digits = match(oneOrMore(digit))
        val number = map(digits) { Number(it.toInt()) }

        val expression = recursive<CharInput, Expression>()

        val openParen = sequence(literal("("), whitespace)
        val closeParen = sequence(whitespace, literal(")"))
        val parenExpression = sequence(openParen, expression, closeParen)
        val operand = oneOf(number, parenExpression)

        val plus = sequence(whitespace, literal("+"), whitespace)
        val minus = sequence(whitespace, literal("-"), whitespace)

        val addition = sequence(operand, plus, operand) { a, b -> Addition(a, b) }
        val subtraction = sequence(operand, minus, operand) { a, b -> Subtraction(a, b) }

        expression.parser(oneOf(addition, subtraction, operand))

        val statement = sequence(whitespace, expression, whitespace)

        // expression = operand ("+" operand | "-" operand)*
        // operand = number | "(" expression ")"
        //
        // plus = expression "+" operand
        // minus = expression "-" operand
        // operand = number | "(" expression ")"
        // expression = plus | minus | operand

        val separator = oneOf(',', '\n')
        val blankLine = sequence(whitespace, literal("\n"))
        val parser = sequence(statement, zeroOrMore(prefixed(separator, statement)), zeroOrMore(blankLine)) { a, b, _ -> listOf(a) + b }

        return parser.parse(text)
    }
}