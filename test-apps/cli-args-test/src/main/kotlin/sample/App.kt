package sample

import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("cli-args-test") {
    private val flag by flag("f", "flag", help = "Enable some flag")
    private val option by option("o", "option", help = "Some option")
    private val required by option("r", help = "Required option").required()
    private val repeated by option("list", help = "Repeated option").repeated()
    private val choice by oneOf {
        choice(1, "one", help = "Option one")
        choice(2, "two", help = "Option two")
    }.flags().repeated()

    override fun run() {
        println("flag: $flag")
        println("option: $option")
        println("required: $required")
        println("repeated: $repeated")
        println("repeated flags: $choice")
    }
}

fun main(args: Array<String>) = App().run(args)
