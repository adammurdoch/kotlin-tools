package sample

import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("cli-args-test") {
    private val param by parameter("param", help = "Some parameter")
    private val choice by oneOf {
        choice(1, "one", help = "Option one")
        choice(2, "two", help = "Option two")
    }.parameter("choice").optional()
    private val repeated by parameter("list", help = "Repeated option").repeated()

    override fun run() {
        println("flag: $param")
        println("choice: $choice")
        println("repeated: $repeated")
    }
}

fun main(args: Array<String>) = App().run(args)
