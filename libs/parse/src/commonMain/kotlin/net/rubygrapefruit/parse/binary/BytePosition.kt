package net.rubygrapefruit.parse.binary

import kotlin.jvm.JvmInline

@JvmInline
value class BytePosition(val offset: Int) {
    override fun toString(): String {
        return "offset: $offset"
    }
}
