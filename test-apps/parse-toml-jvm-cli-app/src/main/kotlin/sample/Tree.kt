package sample

import net.rubygrapefruit.parse.Position

data class Path(val position: Position, val parts: List<String>) {
    val tail: Path
        get() = Path(position, parts.drop(1))
}

data class KeyValuePairTree(val path: Path, val value: Any)

class TableTree(val path: Path, val pairs: List<KeyValuePairTree>)

class FileTree(val pairs: List<KeyValuePairTree>, val tables: List<TableTree>)
