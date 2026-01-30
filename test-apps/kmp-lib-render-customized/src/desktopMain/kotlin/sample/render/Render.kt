package sample.render

import sample.calc.Addition
import sample.calc.Expression
import sample.calc.Number

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

expect fun terminal(): Terminal

open class Terminal {
    open fun literal(value: Any) {
        print(value)
    }

    open fun operator(value: Any) {
        print(value)
    }

    fun whitespace(value: String) {
        print(value)
    }

    data object Plain : Terminal()
}

