package sample

import net.rubygrapefruit.cli.Runnable
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

fun parser(): Parser<Runnable> {
    val verbose = option("verbose").boolean()
    val run = params(command("run"), verbose, positional("greeting")) { verbose, greeting -> Run(verbose, greeting) }
    val generate = params(command("generate"), verbose) { verbose -> Generate(verbose) }
    val commands = oneOf(run, generate)
    return app("cli-args-app", commands).withHelp()
}

fun main(args: Array<String>) = parser().runApp(args)
