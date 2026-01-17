package net.rubygrapefruit.parse

interface SlicingInput<SLICE> {
    /**
     * @param start inclusive
     * @param end exclusive
     */
    fun get(start: Int, end: Int): SLICE
}