package net.rubygrapefruit.store

interface ContentVisitor {
    fun index(changes: Int) {}

    fun value(name: String, details: ValueInfo) {}

    interface ValueInfo {
        val formatted: String
    }
}
