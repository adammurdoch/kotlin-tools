package net.rubygrapefruit.cli.parser

fun parameter(name: String): Parameter<String> {
    return DefaultParameter(name)
}