package sample

import sample.render.Terminal

data class Pair(val path: Path, val value: Any)

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

    companion object {
        fun of(pairs: List<Pair>): Root {
            val builder = TableBuilder("root")
            for (pair in pairs) {
                builder.add(pair.path, pair.value)
            }
            return Root(builder.nodes())
        }
    }

    private sealed interface Builder {
        fun node(): Node
    }

    private class TableBuilder(val name: String) : Builder {
        private val children = mutableMapOf<String, Builder>()

        override fun node(): Node {
            return Table(name, nodes())
        }

        fun add(path: Path, value: Any) {
            if (path.parts.size == 1) {
                val name = path.parts.first()
                children[name] = LeafBuilder(name, value)
            } else {
                val name = path.parts.first()
                val builder = children.getOrPut(name) { TableBuilder(name) } as TableBuilder
                val tail = Path(path.parts.drop(1))
                builder.add(tail, value)
            }
        }

        fun nodes(): List<Node> {
            return children.map { it.value.node() }
        }
    }

    private class LeafBuilder(val name: String, val value: Any) : Builder {
        override fun node(): Node {
            return Leaf(name, value)
        }
    }
}

class Table(val name: String, val nodes: List<Node>) : Node() {
    override fun renderTo(terminal: Terminal) {
        terminal.whitespace("  ")
        terminal.literal(name)
        terminal.whitespace(" ")
        terminal.operator("{")
        println()
        nodes.forEach { it.renderTo(terminal) }
        terminal.operator("}")
        println()
    }
}

class Leaf(val name: String, val value: Any) : Node() {
    override fun renderTo(terminal: Terminal) {
        terminal.whitespace("  ")
        terminal.literal(name)
        terminal.whitespace(" ")
        terminal.operator("=")
        terminal.whitespace(" ")
        terminal.literal(value)
        println()
    }
}
