package net.rubygrapefruit.store

data class Metadata(val generation: Int, val compactedChanges: Int, val nonCompactedChanges: Int)
