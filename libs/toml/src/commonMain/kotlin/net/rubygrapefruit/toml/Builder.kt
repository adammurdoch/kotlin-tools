package net.rubygrapefruit.toml

import net.rubygrapefruit.parse.Position

internal sealed interface Builder {
    fun value(): TomlValue
}

internal class TableBuilder : Builder {
    private val children = mutableMapOf<String, Builder>()

    override fun value(): TomlTable {
        return TomlTable(nodes())
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

internal class ArrayBuilder : Builder {
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
