package net.rubygrapefruit.toml

import net.rubygrapefruit.parse.Position

sealed interface TomlValue

class Table private constructor(val values: List<Value>) : TomlValue {
    companion object {
        internal fun of(tree: FileTree): Table {
            val builder = TableBuilder()
            builder.addAll(tree.pairs)
            for (section in tree.sections) {
                when (section) {
                    is TableTree -> builder.addTable(section.path).addAll(section.pairs)
                    is TableArrayTree -> builder.addTableToArray(section.path).addAll(section.pairs)
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

        fun addAll(pairs: List<KeyValuePairTree>) {
            for (pair in pairs) {
                add(pair.path, pair.value)
            }
        }

        fun add(path: Path, value: TomlValue) {
            add(path) { name, owner ->
                owner.add(name, path.position, value)
            }
        }

        fun addTable(path: Path): TableBuilder {
            return add(path) { name, owner ->
                owner.table(name, path.position)
            }
        }

        fun addTableToArray(path: Path): TableBuilder {
            return add(path) { name, owner ->
                owner.array(name, path.position).addTable()
            }
        }

        private fun <T> add(path: Path, action: (String, TableBuilder) -> T): T {
            val name = path.parts.first()
            return if (path.parts.size == 1) {
                action(name, this)
            } else {
                val builder = table(name, path.position)
                val tail = path.tail
                builder.add(tail, action)
            }
        }

        private fun add(name: String, position: Position, value: TomlValue) {
            if (children.containsKey(name)) {
                throw IllegalArgumentException("Duplicate key '$name' at position $position")
            }
            children[name] = ValueBuilder(value)
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

        private fun array(name: String, position: Position): ArrayBuilder {
            val builder = children[name]
            if (builder is ArrayBuilder) {
                return builder
            } else if (builder != null) {
                throw IllegalArgumentException("Duplicate key '$name' at position $position")
            }
            val newBuilder = ArrayBuilder()
            children[name] = newBuilder
            return newBuilder
        }

        private fun nodes(): List<Value> {
            return children.map { Value(it.key, it.value.value()) }
        }
    }

    private class ArrayBuilder : Builder {
        private val tables = mutableListOf<TableBuilder>()

        fun addTable(): TableBuilder {
            val builder = TableBuilder()
            tables.add(builder)
            return builder
        }

        override fun value(): TomlValue {
            return TomlArray(tables.map { it.value() })
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
