package sample

import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("cli-args-test") {
    private val flag by flag("f", "flag", help = "Enable some flag")
    private val option by option("o", "option", help = "Some option")
    private val required by option("r", help = "Required option").required()
    private val repeated by option("list", help = "Repeated option").repeated()

    override fun run() {
        println("flag: $flag")
        println("option: $option")
        println("repeated: $repeated")
        println("required: $required")
    }
}

fun main(args: Array<String>) = App().run(args)
