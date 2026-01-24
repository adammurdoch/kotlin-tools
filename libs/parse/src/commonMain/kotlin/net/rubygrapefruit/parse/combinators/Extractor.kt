package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.BoxingInput

/**
 * Extracts a value from an input.
 */
internal interface Extractor<in IN, out OUT> {
    fun extract(input: IN): OUT
}

internal object UnitExtractor : Extractor<Any?, Unit> {
    override fun extract(input: Any?) {
    }
}

internal class NextValueExtractor<IN : BoxingInput<*, OUT>, OUT> : Extractor<IN, OUT> {
    override fun extract(input: IN): OUT {
        return input.getBoxed(0)
    }

    companion object {
        fun <IN, OUT> of(): Extractor<IN, OUT> {
            @Suppress("UNCHECKED_CAST")
            return NextValueExtractor<BoxingInput<*, Any>, Any>() as Extractor<IN, OUT>
        }
    }
}