package sample

import sample.render.Terminal

data class Pair(val path: Path, val value: Any)

class TableTree(val path: Path, val pairs: List<Pair>)

class FileTree(val pairs: List<Pair>, val tables: List<TableTree>)

class Table private constructor(private val values: List<Value>) {
    fun renderTo(terminal: Terminal, indent: String = "") {
        terminal.operator("{")
        println()
        values.forEach {
            it.renderTo(terminal, "$indent  ")
        }
        terminal.whitespace(indent)
        terminal.operator("}")
        println()
    }

    companion object {
        fun of(tree: FileTree): Table {
            val builder = TableBuilder()
            for (pair in tree.pairs) {
                builder.add(pair.path, pair.value)
            }
            for (table in tree.tables) {
                val tableBuilder = builder.addTable(table.path)
                for (pair in table.pairs) {
                    tableBuilder.add(pair.path, pair.value)
                }
            }
            return builder.value()
        }
    }

    private sealed interface Builder {
        fun value(): Any
    }

    private class TableBuilder : Builder {
        private val children = mutableMapOf<String, Builder>()

        override fun value(): Table {
            return Table(nodes())
        }

        fun add(path: Path, value: Any) {
            val name = path.parts.first()
            if (path.parts.size == 1) {
                children[name] = ValueBuilder(value)
            } else {
                val builder = table(name)
                val tail = Path(path.parts.drop(1))
                builder.add(tail, value)
            }
        }

        fun addTable(path: Path): TableBuilder {
            val name = path.parts.first()
            return if (path.parts.size == 1) {
                table(name)
            } else {
                val builder = table(name)
                val tail = Path(path.parts.drop(1))
                builder.addTable(tail)
            }
        }

        private fun table(name: String): TableBuilder {
            return children.getOrPut(name) { TableBuilder() } as TableBuilder
        }

        private fun nodes(): List<Value> {
            return children.map { Value(it.key, it.value.value()) }
        }
    }

    private class ValueBuilder(val value: Any) : Builder {
        override fun value(): Any {
            return value
        }
    }
}

class Value(val name: String, val value: Any) {
    fun renderTo(terminal: Terminal, indent: String) {
        terminal.whitespace(indent)
        terminal.literal(name)
        terminal.whitespace(" ")
        terminal.operator("=")
        terminal.whitespace(" ")
        when (value) {
            is Table -> value.renderTo(terminal, indent)
            else -> {
                terminal.literal(value)
                println()
            }
        }
    }
}
