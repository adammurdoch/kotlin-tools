package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*
import net.rubygrapefruit.parse.stream.Input

internal class MapParser<IN, INTERMEDIATE, OUT>(
    private val parser: Parser<IN, INTERMEDIATE>,
    private val map: (INTERMEDIATE) -> OUT
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {

    override fun withNoResult(): Parser<IN, Unit> {
        return DiscardParser(parser)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return MapCompiledParser(compiler.compile(parser), map)
    }

    internal class MapCompiledParser<IN, INTERMEDIATE, OUT>(val parser: CompiledParser<IN, INTERMEDIATE>, private val map: (INTERMEDIATE) -> OUT) : CompiledParser<IN, OUT> {
        override fun start(start: Position, next: ParseContinuation<IN, OUT>): PullParser<IN> {
            return parser.start(start, MapParserContinuation(map, next))
        }
    }

    private class MapParserContinuation<IN, INTERMEDIATE, OUT>(
        private val map: (INTERMEDIATE) -> OUT,
        next: ParseContinuation<IN, OUT>
    ) : ParseContinuation.MappingParseContinuation<IN, INTERMEDIATE, OUT>(next) {
        override fun map(input: IN, start: Int, end: Int, value: ValueProvider<INTERMEDIATE>): ValueProvider<OUT> {
            return value.map(map)
        }
    }
}