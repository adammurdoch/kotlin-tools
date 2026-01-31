package net.rubygrapefruit.parse

internal interface ExpectationProvider {
    fun expectation(): Expectation

    fun map(map: (Expectation) -> Expectation): ExpectationProvider {
        return MappedExpectationProvider(this, map)
    }

    companion object {
        fun oneOf(first: ExpectationProvider, second: ExpectationProvider): ExpectationProvider {
            return object : ExpectationProvider {
                override fun expectation(): Expectation {
                    return Expectation.oneOf(listOf(first.expectation(), second.expectation()))
                }
            }
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