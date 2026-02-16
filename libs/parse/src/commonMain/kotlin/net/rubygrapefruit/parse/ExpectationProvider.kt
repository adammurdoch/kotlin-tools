package net.rubygrapefruit.parse

internal interface ExpectationProvider {
    fun expectation(): Expectation

    fun map(map: (Expectation) -> Expectation): ExpectationProvider {
        return MappedExpectationProvider(this, map)
    }

    companion object {
        fun oneOfOrNull(providers: List<ExpectationProvider>): ExpectationProvider? {
            return when (providers.size) {
                0 -> null
                1 -> providers.first()
                else -> OneOfListExpectationProvider(providers)
            }
        }

        fun oneOf(providers: List<ExpectationProvider>): ExpectationProvider {
            return when (providers.size) {
                0 -> Expectation.Nothing
                1 -> providers.first()
                else -> OneOfListExpectationProvider(providers)
            }
        }

        fun oneOf(first: ExpectationProvider, second: ExpectationProvider): ExpectationProvider {
            return if (first is Expectation.Nothing) {
                second
            } else if (second is Expectation.Nothing) {
                first
            } else {
                OneOfExpectationProvider(first, second)
            }
        }
    }

    private class OneOfExpectationProvider(val first: ExpectationProvider, val second: ExpectationProvider) : ExpectationProvider {
        override fun toString(): String {
            return "{one-of $first $second}"
        }

        override fun expectation(): Expectation {
            return Expectation.oneOf(listOf(first.expectation(), second.expectation()))
        }
    }

    private class OneOfListExpectationProvider(val providers: List<ExpectationProvider>) : ExpectationProvider {
        override fun toString(): String {
            return "{one-of $providers}"
        }

        override fun expectation(): Expectation {
            return Expectation.oneOf(providers.map { it.expectation() })
        }
    }

    private class MappedExpectationProvider(val provider: ExpectationProvider, val map: (Expectation) -> Expectation) : ExpectationProvider {
        override fun toString(): String {
            return "{mapped $provider}"
        }

        override fun expectation(): Expectation {
            return map(provider.expectation())
        }
    }
}