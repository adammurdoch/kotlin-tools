package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Position
import kotlin.jvm.JvmInline

/**
 * The position of a specific byte in a binary stream.
 */
@JvmInline
value class BytePosition(val offset: Position) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
