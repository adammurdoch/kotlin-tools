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
            add(pair.path, pair)
        }
    }

    fun add(path: Path, builder: Builder) {
        add(path) { name, owner ->
            owner.add(name, path.position, builder)
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

    private fun add(name: String, position: Position, builder: Builder) {
        if (children.containsKey(name)) {
            throw IllegalArgumentException("Duplicate key '$name' at position $position")
        }
        children[name] = builder
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

    private fun nodes(): List<KeyValue> {
        return children.map { KeyValue(it.key, it.value.value()) }
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
