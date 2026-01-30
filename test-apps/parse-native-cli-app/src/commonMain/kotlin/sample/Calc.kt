package sample

import kotlinx.io.buffered
import kotlinx.io.readString
import net.rubygrapefruit.cli.app.CliApp
import net.rubygrapefruit.io.stream.stdin
import sample.calc.BinaryExpression
import sample.calc.Expression
import sample.calc.Number
import sample.calc.Parser
import sample.render.Terminal
import sample.render.terminal

class Calc : CliApp("parse-jvm-cli-app") {
    val args by remainder("args")

    override fun run() {
        val parser = Parser()
        val text = if (args.isNotEmpty()) {
            args.joinToString(" ")
        } else {
            println("Type something...")
            stdin.buffered().readString()
        }
        val result = parser.parse(text).get()
        val terminal = terminal()
        for (expression in result) {
            expression.renderTo(terminal)
            print(" = ")
            expression.evaluate().renderTo(terminal)
            println()
        }
    }

    private fun Expression.renderTo(terminal: Terminal) {
        when (this) {
            is BinaryExpression -> {
                left.renderNestedTo(terminal)
                terminal.whitespace(" ")
                terminal.operator(operator)
                terminal.whitespace(" ")
                right.renderNestedTo(terminal)
            }

            is Number -> {
                terminal.literal(value)
            }
        }
    }

    private fun Expression.renderNestedTo(terminal: Terminal) {
        when (this) {
            is BinaryExpression -> {
                terminal.operator('(')
                renderTo(terminal)
                terminal.operator(')')
            }

            is Number -> {
                renderTo(terminal)
            }
        }
    }
}

fun main(args: Array<String>) = Calc().run(args)
