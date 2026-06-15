package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.CombinatorBuilder
import net.rubygrapefruit.parse.CompiledParser
import net.rubygrapefruit.parse.Parser
import net.rubygrapefruit.parse.combinators.OneOrMoreParser.OneOrMoreCompiledParser
import net.rubygrapefruit.parse.combinators.ZeroOrMoreParser.ZeroOrMoreCompiledParser
import net.rubygrapefruit.parse.stream.Input
import kotlin.jvm.JvmName

internal fun <IN : Input<*>, ITEM> rangeParser(
    range: Range,
    item: Parser<*, ITEM>,
    separator: Parser<*, Unit>?,
    compiler: CombinatorBuilder.Compiler<IN>
): CompiledParser<IN, List<ITEM>> {
    if (separator == null) {
        val singleValueParser = compiler.maybeAsSingleInputParser(item)
        if (singleValueParser != null) {
            return RangeSingleInputCompiledParser(
                range,
                singleValueParser.predicate,
                singleValueParser.expectation,
                ListRangeAccumulator.Empty(singleValueParser.extractor)
            )
        }
    }

    val head = compiler.compile(item)
    val tail = if (separator == null) {
        head
    } else {
        val tail = Sequence2Parser(separator, item) { _, v -> v }
        compiler.compile(tail)
    }
    return when (range) {
        is Range.ZeroOrMoreMore -> ZeroOrMoreCompiledParser(head, tail, ListAccumulator.Empty())
        is Range.OneOrMoreMore -> OneOrMoreCompiledParser(head, tail, ListAccumulator.Empty())
    }
}

@JvmName("rangeParserProduceNothing")
internal fun <IN : Input<*>> rangeParser(
    range: Range,
    item: Parser<*, Unit>,
    separator: Parser<*, Unit>?,
    compiler: CombinatorBuilder.Compiler<IN>
): CompiledParser<IN, Unit> {
    if (separator == null) {
        val singleValueParser = compiler.maybeAsSingleInputParser(item)
        if (singleValueParser != null) {
            return RangeSingleInputCompiledParser(
                range,
                singleValueParser.predicate,
                singleValueParser.expectation,
                UnitRangeAccumulator
            )
        }
    }

    val head = compiler.compile(item)
    val tail = if (separator == null) {
        head
    } else {
        val tail = Sequence2Parser(separator, item) { _, v -> v }
        compiler.compile(tail)
    }
    return when (range) {
        is Range.ZeroOrMoreMore -> ZeroOrMoreCompiledParser(head, tail, UnitAccumulator)
        is Range.OneOrMoreMore -> OneOrMoreCompiledParser(head, tail, UnitAccumulator)
    }
}
