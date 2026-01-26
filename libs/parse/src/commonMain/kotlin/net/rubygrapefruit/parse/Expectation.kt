package net.rubygrapefruit.parse

internal sealed interface Expectation {
    fun accept(visitor: (String) -> Unit)

    data object Nothing : Expectation {
        override fun toString(): String {
            return "{nothing}"
        }

        override fun accept(visitor: (String) -> Unit) {
        }
    }

    class One(val description: String) : Expectation {
        override fun toString(): String {
            return "{expect $description}"
        }

        override fun accept(visitor: (String) -> Unit) {
            visitor(description)
        }
    }

    class OneOf(val expectations: List<Expectation>) : Expectation {
        override fun toString(): String {
            return "{expect $expectations}"
        }

        override fun accept(visitor: (String) -> Unit) {
            for (expectation in expectations) {
                expectation.accept(visitor)
            }
        }

        companion object {
            fun of(first: Expectation, second: Expectation): Expectation {
                return OneOf(listOf(first, second))
            }
        }
    }

    class Not(val expectation: Expectation) : Expectation {
        override fun accept(visitor: (String) -> Unit) {
            expectation.accept { text ->
                visitor("not $text")
            }
        }
    }
}