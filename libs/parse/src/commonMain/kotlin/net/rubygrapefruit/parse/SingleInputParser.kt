package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.Extractor

internal interface SingleInputParser<in IN, out OUT> {
    val predicate: InputPredicate<IN>

    val expectation: Expectation

    val extractor: Extractor<IN, OUT>
}