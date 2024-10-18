package sample

import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("cli-args-test") {
    private val flag by flag("f", "flag", help = "Enable some flag")
    private val option by option("o", "option", help = "Some option")

    override fun run() {
        println("flag: $flag")
        println("option: $option")
    }
}

fun main(args: Array<String>) = App().run(args)
