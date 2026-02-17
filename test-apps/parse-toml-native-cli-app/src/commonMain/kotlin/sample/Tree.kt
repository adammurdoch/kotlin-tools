package sample

data class Path(val parts: List<String>)

data class ValueTree(val path: Path, val value: Any)

class TableTree(val path: Path, val pairs: List<ValueTree>)

class FileTree(val pairs: List<ValueTree>, val tables: List<TableTree>)
