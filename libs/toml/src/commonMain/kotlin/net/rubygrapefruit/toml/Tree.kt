package net.rubygrapefruit.toml

import net.rubygrapefruit.parse.Position

internal data class Path(val position: Position, val parts: List<String>) {
    val tail: Path
        get() = Path(position, parts.drop(1))
}

internal data class KeyValuePairTree(val path: Path, val value: TomlValue)

internal class TableTree(val path: Path, val pairs: List<KeyValuePairTree>)

internal class FileTree(val pairs: List<KeyValuePairTree>, val tables: List<TableTree>)
