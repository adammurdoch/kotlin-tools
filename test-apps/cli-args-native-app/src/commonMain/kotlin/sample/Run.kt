package sample

import net.rubygrapefruit.cli.app.Runnable
import net.rubygrapefruit.cli.app.runApp
import net.rubygrapefruit.cli.parser.*

class Run(val verbose: Boolean, val greeting: String) : Runnable {
    override fun run() {
        if (verbose) {
            print("> ")
        }
        println(greeting)
    }
}

class Generate(val verbose: Boolean) : Runnable {
    override fun run() {
        if (verbose) {
            print("> ")
        }
        println("generate")
    }
}

class Help : Runnable {
    override fun run() {
        println("help")
    }
}

fun parser(): Parser<Runnable> {
    val help = params(option("help").boolean().required()) { _ -> Help() }
    val verbose = option("verbose").boolean()
    val run = params(command("run"), verbose, positional("greeting")) { _, verbose, greeting -> Run(verbose, greeting) }
    val generate = params(command("generate"), verbose) { _, verbose -> Generate(verbose) }
    return oneOf(help, run, generate)
}

fun main(args: Array<String>) = parser().runApp(args)
