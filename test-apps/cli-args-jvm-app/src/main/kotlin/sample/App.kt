package sample

import net.rubygrapefruit.cli.app.Runnable
import net.rubygrapefruit.cli.app.runApp
import net.rubygrapefruit.cli.parser.*

class App(val verbose: Boolean, val greeting: String) : Runnable {
    override fun run() {
        if (verbose) {
            print("> ")
        }
        println(greeting)
    }
}

class Help(val verbose: Boolean) : Runnable {
    override fun run() {
        if (verbose) {
            print("> ")
        }
        println("help")
    }
}

fun parser(): Parser<Runnable> {
    val help = allOf(literal("help"), option("verbose").boolean()) { _, verbose -> Help(verbose) }
    val app = allOf(literal("run"), option("verbose").boolean(), parameter("greeting")) { _, verbose, greeting -> App(verbose, greeting) }
    return oneOf(help, app)
}

fun main(args: Array<String>) = parser().runApp(args)
