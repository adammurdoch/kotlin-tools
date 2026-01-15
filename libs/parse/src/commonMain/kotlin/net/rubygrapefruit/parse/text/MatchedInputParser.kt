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
                next.matched(matched.count, "??")
            }
        }
    }
}