package sample

import net.rubygrapefruit.cli.app.CliApp
import sample.render.terminal

class App : CliApp("parse-toml-jvm-cli-app") {
    private val file by file(true).option("file").required()

    override fun run() {
        val root = Parser().parse(file)
        val terminal = terminal()
        root.renderTo(terminal)
    }
}

fun main(args: Array<String>) = App().run(args)