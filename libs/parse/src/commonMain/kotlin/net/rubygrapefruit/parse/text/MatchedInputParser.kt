package net.rubygrapefruit.parse.text

import net.rubygrapefruit.parse.*

internal class MatchedInputParser(private val parser: Parser<CharInput, *>) : Parser<CharInput, String>, TypedInputCombinatorBuilder<CharStream, String> {
    override fun compile(compiler: CombinatorBuilder.Compiler<CharStream>): CompiledParser<CharStream, String> {
        return MatchedInputCompiledParser(compiler.compile(parser))
    }

    private class MatchedInputCompiledParser(private val parser: CompiledParser<CharStream, *>) : CompiledParser<CharStream, String> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = parser.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = parser.expectation

        override fun <NEXT> start(next: ParseContinuation<CharStream, String, NEXT>): PullParser<CharStream, NEXT> {
            return parser.start { matched ->
                PullParser.RequireMore(matched.end, CollectMatchedInputPullParser(matched.end - matched.start, next))
            }
        }
    }

    private class CollectMatchedInputPullParser<NEXT>(
        private val length: Int,
        private val next: ParseContinuation<CharStream, String, NEXT>
    ) : PullParser<CharStream, NEXT> {
        override val expectation: Expectation
            get() = Expectation.Nothing

        override fun parse(input: CharStream, max: Int): PullParser.Result<CharStream, NEXT> {
            return next.matched(-length, 0, input.get(-length, 0))
        }
    }
}