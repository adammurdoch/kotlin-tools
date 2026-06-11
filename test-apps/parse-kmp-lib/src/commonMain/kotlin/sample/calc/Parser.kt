package sample.calc

import net.rubygrapefruit.parse.ParseResult
import net.rubygrapefruit.parse.combinators.*
import net.rubygrapefruit.parse.text.*

class Parser {
    fun parse(text: String): ParseResult<*, List<Expression>> {
        val optionalWhitespace = zeroOrMore(literal(" "))

        val number = map(integer()) { Number(it) }

        val expression = recursive<TextInput, Expression>()

        val openParen = sequence(literal("("), optionalWhitespace)
        val closeParen = sequence(optionalWhitespace, literal(")"))
        val parenExpression = sequence(openParen, expression, closeParen)
        val operand = oneOf(number, parenExpression)

        val plus = sequence(optionalWhitespace, literal("+"), optionalWhitespace)
        val minus = sequence(optionalWhitespace, literal("-"), optionalWhitespace)

        val addition = sequence(operand, plus, operand) { a, b -> Addition(a, b) }
        val subtraction = sequence(operand, minus, operand) { a, b -> Subtraction(a, b) }

        expression.parser(oneOf(addition, subtraction, operand))

        val statement = sequence(optionalWhitespace, expression, optionalWhitespace)

        // expression = operand ("+" operand | "-" operand)*
        // operand = number | "(" expression ")"
        //
        // plus = expression "+" operand
        // minus = expression "-" operand
        // operand = number | "(" expression ")"
        // expression = plus | minus | operand

        val separator = oneOf(',', '\n')
        val blankLine = sequence(optionalWhitespace, literal("\n"))
        val blankLines = zeroOrMore(blankLine)
        val statements = oneOrMore(statement, separator = separator)
        val parser = sequence(blankLines, statements, blankLines)

        return parser.parse(text)
    }
}