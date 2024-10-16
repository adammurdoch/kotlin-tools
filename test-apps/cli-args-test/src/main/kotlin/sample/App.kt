package sample

import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("cli-args-test") {
    private val flag by flag("f", "flag", help = "Enable some flag")

    override fun run() {
        println("flag: $flag")
    }
}

fun main(args: Array<String>) = App().run(args)
