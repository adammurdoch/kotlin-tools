package net.rubygrapefruit.store

interface ContentVisitor {
    /**
     * Visits information about the store.
     */
    fun store(detail: StoreInfo) {}

    /**
     * Visits information about a value in the store.
     */
    fun value(name: String, details: ValueInfo) {}

    class StoreInfo(val totalChanges: Int, val generation: Int, val nonCompactedChanges: Int)

    interface ValueInfo {
        val formatted: String
    }
}
