package sample

import sample.render.Terminal

sealed class Node {
    abstract fun renderTo(terminal: Terminal)
}

class Root(val nodes: List<Node>) : Node() {
    override fun renderTo(terminal: Terminal) {
        terminal.operator("{")
        println()
        nodes.forEach { it.renderTo(terminal) }
        terminal.operator("}")
        println()
    }
}

class Leaf(val name: String, val value: String) : Node() {
    override fun renderTo(terminal: Terminal) {
        terminal.whitespace("  ")
        terminal.literal(name)
        terminal.whitespace(" ")
        terminal.operator("=")
        terminal.whitespace(" ")
        terminal.literal("\"" + value + "\"")
        println()
    }
}
