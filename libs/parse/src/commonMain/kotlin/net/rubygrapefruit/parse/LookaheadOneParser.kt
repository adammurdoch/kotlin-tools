package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.Extractor

internal interface LookaheadOneParser<in IN, out OUT> {
    val predicate: InputPredicate<IN>

    val extractor: Extractor<IN, OUT>
}