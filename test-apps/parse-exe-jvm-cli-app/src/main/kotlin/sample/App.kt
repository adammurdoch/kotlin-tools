package sample

import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("parse-exe-jvm-cli-app") {
    private val file by file(true).option("file").required()

    override fun run() {
        val details = Parser().parse(file)
        println(details)
    }
}

fun main(args: Array<String>) = App().run(args)