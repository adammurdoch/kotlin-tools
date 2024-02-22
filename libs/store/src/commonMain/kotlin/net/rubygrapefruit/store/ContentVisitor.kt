package net.rubygrapefruit.store

interface ContentVisitor {
    fun index(updates: Int) {}

    fun value(name: String, details: ValueInfo) {}

    interface ValueInfo {
        val formatted: String
    }
}
