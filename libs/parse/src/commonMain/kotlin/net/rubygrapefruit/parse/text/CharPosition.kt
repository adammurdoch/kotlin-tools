package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Position

class CharPosition(val offset: Position, val line: Int, val col: Int) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
