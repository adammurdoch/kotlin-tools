package net.rubygrapefruit.toml

import net.rubygrapefruit.parse.Position

internal data class Path(val position: Position, val parts: List<String>) {
    val tail: Path
        get() = Path(position, parts.drop(1))
}

internal data class KeyValuePairTree(val path: Path, val value: TomlValue)

internal sealed interface SectionTree

internal class TableTree(val path: Path, val pairs: List<KeyValuePairTree>) : SectionTree

internal class TableArrayTree(val path: Path, val pairs: List<KeyValuePairTree>) : SectionTree

internal class FileTree(val pairs: List<KeyValuePairTree>, val sections: List<SectionTree>) {
    fun resolve(): TomlTable {
        val builder = TableBuilder()
        builder.addAll(pairs)
        for (section in sections) {
            when (section) {
                is TableTree -> builder.addTable(section.path).addAll(section.pairs)
                is TableArrayTree -> builder.addTableToArray(section.path).addAll(section.pairs)
            }
        }
        return builder.value()
    }
}
