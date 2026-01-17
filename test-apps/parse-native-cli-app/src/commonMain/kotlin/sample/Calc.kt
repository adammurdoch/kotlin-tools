package sample

import net.rubygrapefruit.cli.app.CliApp
import sample.calc.*
import sample.calc.Number

class Calc : CliApp("parse-jvm-cli-app") {
    val args by remainder("args")

    override fun run() {
        val parser = Parser()
        val result = parser.parse(args).get()
        print("expression: ")
        result.render()
        println()
        println("value: ${result.evaluate()}")
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
