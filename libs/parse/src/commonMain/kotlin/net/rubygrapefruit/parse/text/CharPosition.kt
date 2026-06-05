package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.Position

/**
 * The position of a specific character in a text stream.
 */
class CharPosition(val position: Position, val line: Int, val col: Int) {
    override fun toString(): String {
        return "position: $position"
    }
}
