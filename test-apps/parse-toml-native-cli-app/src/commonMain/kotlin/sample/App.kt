package sample

import net.rubygrapefruit.cli.app.appMain
import net.rubygrapefruit.cli.files.inputFile
import net.rubygrapefruit.cli.parser.action
import net.rubygrapefruit.cli.parser.app
import net.rubygrapefruit.cli.parser.positional
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.text.file.parse
import net.rubygrapefruit.toml.Table
import net.rubygrapefruit.toml.TomlParser
import net.rubygrapefruit.toml.Value
import sample.render.Terminal
import sample.render.terminal

fun main(args: Array<String>) = appMain(args) {
    val action = action(positional("file", inputFile())) { file ->
        val root = TomlParser().parse(file)
        val terminal = terminal()
        root.renderTo(terminal)
    }

    app("parse-toml-cli-app", action)
}

fun TomlParser.parse(file: RegularFile): Table {
    return parser.parse(file).get()
}

fun Table.renderTo(terminal: Terminal, indent: String = "") {
    terminal.operator("{")
    println()
    values.forEach {
        it.renderTo(terminal, "$indent  ")
    }
    terminal.whitespace(indent)
    terminal.operator("}")
    println()
}

fun Value.renderTo(terminal: Terminal, indent: String) {
    terminal.whitespace(indent)
    terminal.literal(name)
    terminal.whitespace(" ")
    terminal.operator("=")
    terminal.whitespace(" ")
    val value = value
    when (value) {
        is Table -> value.renderTo(terminal, indent)
        else -> {
            terminal.literal(value)
            println()
        }
    }
}

