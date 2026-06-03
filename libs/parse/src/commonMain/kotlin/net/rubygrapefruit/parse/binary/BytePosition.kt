package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Position
import kotlin.jvm.JvmInline

@JvmInline
value class BytePosition(val offset: Position) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
