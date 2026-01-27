package net.rubygrapefruit.parse

internal interface ExpectationProvider {
    fun expectation(): Expectation

    fun map(map: (Expectation) -> Expectation): ExpectationProvider {
        val self = this
        return object : ExpectationProvider {
            override fun expectation(): Expectation {
                return map(self.expectation())
            }
        }
    }
}