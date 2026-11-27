package net.rubygrapefruit.toml

import net.rubygrapefruit.parse.Position

sealed interface TomlValue

class Table private constructor(val values: List<Value>) : TomlValue {
    companion object {
        internal fun of(tree: FileTree): Table {
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
        fun value(): TomlValue
    }

    private class TableBuilder : Builder {
        private val children = mutableMapOf<String, Builder>()

        override fun value(): Table {
            return Table(nodes())
        }

        fun add(path: Path, value: TomlValue) {
            val name = path.parts.first()
            if (path.parts.size == 1) {
                if (children.containsKey(name)) {
                    throw IllegalArgumentException("Duplicate key '$name' at position ${path.position}")
                }
                children[name] = ValueBuilder(value)
            } else {
                val builder = table(name, path.position)
                val tail = path.tail
                builder.add(tail, value)
            }
        }

        fun addTable(path: Path): TableBuilder {
            val name = path.parts.first()
            return if (path.parts.size == 1) {
                table(name, path.position)
            } else {
                val builder = table(name, path.position)
                val tail = path.tail
                builder.addTable(tail)
            }
        }

        private fun table(name: String, position: Position): TableBuilder {
            val builder = children[name]
            if (builder is TableBuilder) {
                return builder
            } else if (builder != null) {
                throw IllegalArgumentException("Duplicate key '$name' at position $position")
            }
            val newBuilder = TableBuilder()
            children[name] = newBuilder
            return newBuilder
        }

        private fun nodes(): List<Value> {
            return children.map { Value(it.key, it.value.value()) }
        }
    }

    private class ValueBuilder(val value: TomlValue) : Builder {
        override fun value(): TomlValue {
            return value
        }
    }
}

class TomlString(val value: String) : TomlValue

class TomlInt(val value: Int) : TomlValue

class TomlBoolean(val value: Boolean) : TomlValue

class TomlArray(val value: List<TomlValue>) : TomlValue

class Value(val name: String, val value: TomlValue)
