package sample

import kotlinx.io.buffered
import kotlinx.io.readString
import net.rubygrapefruit.cli.app.CliApp
import net.rubygrapefruit.io.stream.stdin
import sample.calc.BinaryExpression
import sample.calc.Expression
import sample.calc.Number
import sample.calc.Parser

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
        for (expression in result) {
            expression.render()
            print(" = ")
            println(expression.evaluate())
        }
    }

    private fun Expression.render() {
        when (this) {
            is BinaryExpression -> {
                left.renderNested()
                print(' ')
                print(operator)
                print(' ')
                right.renderNested()
            }

            is Number -> {
                print(value)
            }
        }
    }

    private fun Expression.renderNested() {
        when (this) {
            is BinaryExpression -> {
                print('(')
                render()
                print(')')
            }

            is Number -> {
                render()
            }
        }
    }
}

fun main(args: Array<String>) = Calc().run(args)
