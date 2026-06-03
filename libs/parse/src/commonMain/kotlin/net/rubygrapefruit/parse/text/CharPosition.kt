package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Offset

class CharPosition(val offset: Offset, val line: Int, val col: Int) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
