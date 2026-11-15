package sample

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.app.appMain
import net.rubygrapefruit.cli.parser.*

class Run(val verbose: Boolean, val greeting: String) : Runnable {
    override fun Runnable.Context.run() {
        if (verbose) {
            print("> ")
        }
        println("hello $greeting")
    }
}

class Generate(val verbose: Boolean) : Runnable {
    override fun Runnable.Context.run() {
        if (verbose) {
            print("> ")
        }
        println("generate")
    }
}

fun main(args: Array<String>) = appMain(args) {
    val verbose = option("verbose", summary = "Use verbose output").boolean()
    val run = params(command("run"), verbose, positional("greeting"), summary = "Show a greeting") { verbose, greeting -> Run(verbose, greeting) }
    val generate = params(command("generate"), verbose, summary ="Generate something") { verbose -> Generate(verbose) }
    val commands = oneOf(run, generate)
    app("cli-args-app", commands, summary = "A sample app")
}
