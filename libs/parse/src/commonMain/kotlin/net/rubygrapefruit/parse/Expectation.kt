package net.rubygrapefruit.parse

internal sealed interface Expectation {
    fun accept(visitor: (String) -> Unit)

    data object Nothing : Expectation {
        override fun accept(visitor: (String) -> Unit) {
        }
    }

    class One(val description: String) : Expectation {
        override fun accept(visitor: (String) -> Unit) {
            visitor(description)
        }
    }

    class OneOf(val expectations: List<Expectation>) : Expectation {
        override fun accept(visitor: (String) -> Unit) {
            for (expectation in expectations) {
                expectation.accept(visitor)
            }
        }
    }
}