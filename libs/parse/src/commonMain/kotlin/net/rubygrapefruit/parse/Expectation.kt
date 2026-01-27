package net.rubygrapefruit.parse

internal sealed interface Expectation : ExpectationProvider {
    fun accept(visitor: (String) -> Unit)

    override fun expectation(): Expectation {
        return this
    }

    companion object {
        fun oneOf(expectations: List<Expectation>): Expectation {
            val effective = expectations.filter { it !is Nothing }
            return when (effective.size) {
                0 -> Nothing
                1 -> effective.first()
                else -> OneOf(effective)
            }
        }

        fun oneOf(first: Expectation, second: Expectation): Expectation {
            return oneOf(listOf(first, second))
        }
    }

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

    private class OneOf(val expectations: List<Expectation>) : Expectation {
        override fun toString(): String {
            return "{expect $expectations}"
        }

        override fun accept(visitor: (String) -> Unit) {
            for (expectation in expectations) {
                expectation.accept(visitor)
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