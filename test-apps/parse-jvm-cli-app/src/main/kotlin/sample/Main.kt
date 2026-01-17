package sample

import net.rubygrapefruit.cli.app.CliApp
import net.rubygrapefruit.parse.ParseResult
import sample.calc.*
import sample.calc.Number

class App : CliApp("parse-jvm-cli-app") {
    val args by remainder("args")

    override fun run() {
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
}

fun main(args: Array<String>) = App().run(args)

