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
    val help = params(option("help", "h").boolean().required()) { Help() }
    val verbose = option("verbose").boolean()
    val run = params(command("run"), verbose, positional("greeting")) { verbose, greeting -> Run(verbose, greeting) }
    val generate = params(command("generate"), verbose) { verbose -> Generate(verbose) }
    val commands = oneOf(help, run, generate)
    return app("cli-args-app", commands)
}

fun main(args: Array<String>) = parser().runApp(args)
