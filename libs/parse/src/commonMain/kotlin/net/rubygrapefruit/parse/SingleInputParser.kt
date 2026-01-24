package net.rubygrapefruit.parse

internal interface SingleInputParser<in IN> {
    /**
     * What does this parser expect as the next input value?
     */
    val expectation: Expectation

    fun match(input: IN, index: Int): Boolean
}