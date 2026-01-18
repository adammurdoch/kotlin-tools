package sample

import kotlinx.io.buffered
import kotlinx.io.readString
import net.rubygrapefruit.cli.app.CliApp
import net.rubygrapefruit.io.stream.stdin
import sample.calc.*
import sample.calc.Number

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
            print("expression: ")
            expression.render()
            println()
            println("value: ${expression.evaluate()}")
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
}

fun main(args: Array<String>) = Calc().run(args)
