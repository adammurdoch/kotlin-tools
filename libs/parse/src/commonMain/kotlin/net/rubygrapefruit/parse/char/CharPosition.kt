package net.rubygrapefruit.parse.char

class CharPosition(val offset: Int, val line: Int, val col: Int) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
