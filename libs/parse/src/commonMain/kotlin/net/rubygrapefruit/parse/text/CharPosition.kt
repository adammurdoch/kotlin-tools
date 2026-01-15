package net.rubygrapefruit.parse.text

class CharPosition(val offset: Int, val line: Int, val col: Int) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
