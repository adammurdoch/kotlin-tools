package net.rubygrapefruit.cli.parser

interface StringPositional : Positional<String> {
    fun repeated(): Positional<List<String>>
}