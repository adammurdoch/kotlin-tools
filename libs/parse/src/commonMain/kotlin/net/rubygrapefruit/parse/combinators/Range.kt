package net.rubygrapefruit.parse.combinators

internal sealed interface Range {
    val diagnostic: String

    val min: Int

    data object ZeroOrMoreMore : Range {
        override val diagnostic: String
            get() = "zero-or-more"

        override val min: Int
            get() = 0
    }

    data object OneOrMoreMore : Range {
        override val diagnostic: String
            get() = "one-or-more"

        override val min: Int
            get() = 1
    }
}