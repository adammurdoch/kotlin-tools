package net.rubygrapefruit.parse.combinators

import kotlin.math.min

internal sealed interface Range {
    val diagnostic: String

    val min: Int

    fun remaining(matched: Int, max: Int): Int

    fun stop(matched: Int): Boolean

    data object ZeroOrMoreMore : Range {
        override val diagnostic: String
            get() = "zero-or-more"

        override val min: Int
            get() = 0

        override fun remaining(matched: Int, max: Int): Int {
            return max
        }

        override fun stop(matched: Int): Boolean {
            return false
        }
    }

    data object OneOrMoreMore : Range {
        override val diagnostic: String
            get() = "one-or-more"

        override val min: Int
            get() = 1

        override fun remaining(matched: Int, max: Int): Int {
            return max
        }

        override fun stop(matched: Int): Boolean {
            return false
        }
    }

    data class Exact(val count: Int) : Range {
        override val diagnostic: String
            get() = "repeat $count"

        override val min: Int
            get() = count

        override fun remaining(matched: Int, max: Int): Int {
            return min(max, count - matched)
        }

        override fun stop(matched: Int): Boolean {
            return matched == count
        }
    }
}