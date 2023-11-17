package net.rubygrapefruit.file

import kotlin.jvm.JvmInline

@JvmInline
value class Timestamp(val nanos: Long) {
    companion object {
        private const val NANOS_PER_SECOND = 1000 * 1000 * 1000

        fun of(seconds: Long, nanos: Long): Timestamp {
            return Timestamp(seconds * NANOS_PER_SECOND + nanos)
        }
    }
}
