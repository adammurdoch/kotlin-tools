package sample.render

import sample.calc.Expression
import platform.posix.isatty

actual fun render(expression: Expression) {
    if (isatty(1) != 0) {
        print("\u001B[31m")
        print(expression)
        println("\u001B[39m")
    } else {
        println(expression)
    }
}