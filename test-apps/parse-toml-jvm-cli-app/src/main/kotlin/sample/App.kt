package sample

import net.rubygrapefruit.cli.app.appMain
import net.rubygrapefruit.cli.files.inputFile
import net.rubygrapefruit.cli.parser.action
import net.rubygrapefruit.cli.parser.app
import net.rubygrapefruit.cli.parser.positional
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.text.file.parse
import net.rubygrapefruit.toml.*
import sample.render.Terminal
import sample.render.terminal

fun main(args: Array<String>) = appMain(args) {
    val action = action(positional("file", inputFile())) { file ->
        val root = TomlParser().parse(file)
        val terminal = terminal()
        root.renderTo(terminal)
        println()
    }

    app("parse-toml-cli-app", action)
}

fun TomlParser.parse(file: RegularFile): TomlTable {
    return parser.parse(file).get()
}

fun TomlTable.renderTo(terminal: Terminal, indent: String = "") {
    terminal.operator("{")
    println()
    values.forEach {
        it.renderTo(terminal, "$indent  ")
        println()
    }
    terminal.whitespace(indent)
    terminal.operator("}")
}

fun Value.renderTo(terminal: Terminal, indent: String) {
    terminal.whitespace(indent)
    terminal.identifier(name)
    terminal.whitespace(" ")
    terminal.operator("=")
    terminal.whitespace(" ")
    value.renderTo(terminal, indent)
}

fun TomlValue.renderTo(terminal: Terminal, indent: String) {
    when (this) {
        is TomlTable -> renderTo(terminal, indent)
        is TomlString -> {
            terminal.operator("'")
            terminal.literal(value)
            terminal.operator("'")
        }

        is TomlInt -> terminal.literal(value)
        is TomlBoolean -> terminal.literal(value)
        is TomlArray -> {
            terminal.operator("[")
            value.forEachIndexed { index, element ->
                if (index > 0) {
                    terminal.operator(",")
                    terminal.whitespace(" ")
                }
                element.renderTo(terminal, "$indent  ")
            }
            terminal.operator("]")
        }
    }
}
