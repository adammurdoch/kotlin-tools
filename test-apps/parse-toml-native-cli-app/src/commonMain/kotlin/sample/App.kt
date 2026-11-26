package sample

import net.rubygrapefruit.cli.app.appMain
import net.rubygrapefruit.cli.parser.action
import net.rubygrapefruit.cli.parser.app
import net.rubygrapefruit.cli.files.inputFile
import net.rubygrapefruit.cli.parser.positional
import sample.render.terminal

fun main(args: Array<String>) = appMain(args) {
    val action = action(positional("file", inputFile())) { file ->
        val root = TomlParser().parse(file)
        val terminal = terminal()
        root.renderTo(terminal)
    }

    app("parse-toml-cli-app", action)
}