package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.stream.BoxingInput

/**
 * Extracts a value from the next input value.
 */
internal interface Extractor<in IN, out OUT> {
    fun extract(input: IN, index: Int): OUT
}

internal object UnitExtractor : Extractor<Any?, Unit> {
    override fun extract(input: Any?, index: Int) {
    }
}

internal class NextValueExtractor<IN : BoxingInput<*, OUT>, OUT> : Extractor<IN, OUT> {
    override fun extract(input: IN, index: Int): OUT {
        return input.getBoxed(index)
    }

    companion object {
        fun <IN, OUT> of(): Extractor<IN, OUT> {
            @Suppress("UNCHECKED_CAST")
            return NextValueExtractor<BoxingInput<*, Any>, Any>() as Extractor<IN, OUT>
        }
    }
}