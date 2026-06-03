package net.rubygrapefruit.parse.binary

import net.rubygrapefruit.parse.Offset
import kotlin.jvm.JvmInline

@JvmInline
value class BytePosition(val offset: Offset) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
