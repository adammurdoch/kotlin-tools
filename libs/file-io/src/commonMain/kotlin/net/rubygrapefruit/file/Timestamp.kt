package net.rubygrapefruit.file

import kotlin.jvm.JvmInline

@JvmInline
value class Timestamp(private val nanos: Long) {
    companion object {
        private const val nanosPerSecond = 1000 * 1000 * 1000

        fun of(seconds: Long, nanos: Long): Timestamp {
            return Timestamp(seconds * nanosPerSecond + nanos)
        }
    }
}
