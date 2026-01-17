package sample

import net.rubygrapefruit.parse.ParseResult
import sample.calc.Addition
import sample.calc.Expression
import sample.calc.Number
import sample.calc.Parser
import sample.calc.Subtraction

fun main(args: Array<String>) {
    val parser = Parser()
    val result = parser.parse(args)

    when (result) {
        is ParseResult.Fail -> throw IllegalArgumentException("${result.position}: ${result.message}")
        is ParseResult.Success -> {
            print("expression: ")
            result.value.render()
            println()
            println("value: ${result.value.evaluate()}")
        }
    }
}

private fun Expression.render() {
    when (this) {
        is Addition -> {
            left.render()
            print(" + ")
            right.render()
        }
        is Subtraction -> {
            left.render()
            print(" - ")
            right.render()
        }
        is Number -> {
            print("($value)")
        }
    }
}
