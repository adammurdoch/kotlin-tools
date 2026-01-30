package sample

import sample.calc.Failure
import sample.calc.Parser
import sample.calc.Success
import sample.render.render
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
