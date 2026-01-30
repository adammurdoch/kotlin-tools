package sample

import sample.calc.*
import sample.calc.Number
import sample.render.Terminal
import sample.render.terminal
import sample.system.reportSystemInfo

fun main(args: Array<String>) {
    reportSystemInfo()
    val parser = Parser()
    val result = parser.parse(args)
    println("Input: ${args.joinToString(" ")}")
    when (result) {
        is Success -> {
            render(result.expression)
            print(" = ")
            render(result.expression.evaluate())
            println()
        }

        is Failure -> println(result.message)
    }
}

fun render(expression: Expression) {
    val renderer = terminal()
    expression.renderTo(renderer)
}

private fun Expression.renderTo(terminal: Terminal) {
    when (this) {
        is Number -> terminal.literal(value)
        is Addition -> {
            left.renderTo(terminal)
            terminal.whitespace(" ")
            terminal.operator("+")
            terminal.whitespace(" ")
            right.renderTo(terminal)
        }
    }
}
