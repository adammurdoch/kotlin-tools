package net.rubygrapefruit.parse.combinators

import net.rubygrapefruit.parse.*

internal class MapParser<IN, INTERMEDIATE, OUT>(
    private val parser: Parser<IN, INTERMEDIATE>,
    private val map: (INTERMEDIATE) -> OUT
) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        return MapCompiledParser(compiler.compile(parser), map)
    }

    internal class MapCompiledParser<IN, INTERMEDIATE, OUT>(val parser: CompiledParser<IN, INTERMEDIATE>, private val map: (INTERMEDIATE) -> OUT) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = parser.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = parser.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return parser.start(next.expectation) { length, value ->
                next.next(length, map(value))
            }
        }
    }
}