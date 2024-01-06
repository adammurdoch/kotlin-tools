package net.rubygrapefruit.store

interface ContentVisitor {
    fun value(name: String, details: ValueInfo) {}

    class ValueInfo()
}
