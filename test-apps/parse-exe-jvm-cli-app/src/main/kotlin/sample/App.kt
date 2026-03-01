package sample

import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("parse-exe-jvm-cli-app") {
    private val files by file(true).option("file").repeated()

    override fun run() {
        val parser = Parser()
        for (file in files) {
            println(file)
            val details = parser.parse(file)
            for (image in details) {
                println(image)
            }
        }
    }
}

fun main(args: Array<String>) = App().run(args)