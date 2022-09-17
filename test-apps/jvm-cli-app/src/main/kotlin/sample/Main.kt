package sample

import sample.calc.Failure
import sample.calc.Parser
import sample.calc.Success
import sample.system.reportSystemInfo

fun main(args: Array<String>) {
    reportSystemInfo()
    val parser = Parser()
    val result = parser.parse(args)
    when (result) {
        is Success -> {
            println("Expression: ${result.expression}")
            println("Result: ${result.expression.evaluate()}")
        }
        is Failure -> println("Expected ${result.expected}, found: ${result.remaining.take(30)}")
    }
}
