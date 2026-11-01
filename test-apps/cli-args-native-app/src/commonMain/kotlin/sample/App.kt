package sample

import net.rubygrapefruit.cli.app.Runnable
import net.rubygrapefruit.cli.app.runApp
import net.rubygrapefruit.cli.parser.Parser
import net.rubygrapefruit.cli.parser.allOf
import net.rubygrapefruit.cli.parser.option
import net.rubygrapefruit.cli.parser.parameter

class App(val help: Boolean, val greeting: String) : Runnable {
    override fun run() {
        if (help) {
            println("help")
        } else {
            println(greeting)
        }
    }
}

fun parser(): Parser<App> {
    return allOf(option("help").boolean(), parameter("greeting")) { help, greeting -> App(help, greeting) }
}

fun main(args: Array<String>) = parser().runApp(args)
