package net.rubygrapefruit.parse

import kotlin.jvm.JvmInline

@JvmInline
value class Offset(val value: Int) {
    operator fun plus(count: Int): Offset {
        return Offset(value + count)
    }

    companion object {
        val Zero = Offset(0)
    }
}

operator fun Int.minus(offset: Offset): Int {
    return this - offset.value
}

operator fun Int.plus(offset: Offset): Int {
    return this + offset.value
}
