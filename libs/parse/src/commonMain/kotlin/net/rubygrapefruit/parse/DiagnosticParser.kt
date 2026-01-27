package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.DiscardParser

internal class DiagnosticParser<IN, OUT> private constructor(
    private val parser: Parser<IN, OUT>,
    private val logger: Logger
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    constructor(parser: Parser<IN, OUT>, log: Boolean) : this(parser, if (log) Logger.Active(0) else Logger.Disabled)

    override fun withNoResult(): Parser<IN, Unit> {
        return DiscardParser(parser)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val parser = if (parser is CombinatorBuilder<*>) {
            @Suppress("UNCHECKED_CAST")
            (parser as CombinatorBuilder<OUT>).compile(DiagnosticCompiler(compiler, logger.nested()))
        } else {
            compiler.compile(parser)
        }
        return DiagnosticCompiledParser(parser, logger)
    }

    private class DiagnosticCompiler<IN>(private val compiler: CombinatorBuilder.Compiler<IN>, private val logger: Logger) : CombinatorBuilder.Compiler<IN> {
        override fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
            return compiler.compile(DiagnosticParser(parser, logger))
        }

        override fun <OUT> compileRecursive(outer: Parser<*, OUT>, compiledOuter: CompiledParser<IN, OUT>, parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
            return compiler.compileRecursive(outer, compiledOuter, parser)
        }

        override fun compileWithNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit> {
            return compiler.compileWithNoResult(DiagnosticParser(parser, logger))
        }

        override fun maybeAsSingleInputParser(parser: Parser<*, *>): SingleInputParser<IN>? {
            return compiler.maybeAsSingleInputParser(parser)
        }
    }

    private class DiagnosticCompiledParser<IN, OUT>(private val parser: CompiledParser<IN, OUT>, private val logger: Logger) : CompiledParser<IN, OUT> {
        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return DiagnosticPullParser(parser.start(next), logger)
        }
    }

    private class DiagnosticPullParser<IN, OUT>(private val parser: PullParser<IN, OUT>, private val logger: Logger) : PullParser<IN, OUT> {
        override fun toString(): String {
            return parser.toString()
        }

        override fun stop(): PullParser.Failed {
            return parser.stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            val parserState = parser.toString()
            val result = parser.parse(input, max)
            logger.log("-> $parserState parse(max=$max) => $result")
            return when (result) {
                is PullParser.Matched -> result

                is PullParser.Failed -> {
                    require(result.index <= max)
                    result
                }

                is PullParser.RequireMore -> {
                    require(result.advance <= max)
                    PullParser.RequireMore(result.advance, DiagnosticPullParser(result.parser, logger))
                }
            }
        }
    }

    private sealed class Logger {
        abstract fun log(message: String)

        abstract fun nested(): Logger

        class Active(val depth: Int) : Logger() {
            override fun nested(): Logger {
                return Active(depth + 1)
            }

            override fun log(message: String) {
                repeat(depth) {
                    print("  ")
                }
                println(message)
            }
        }

        data object Disabled : Logger() {
            override fun nested(): Logger {
                return this
            }

            override fun log(message: String) {
            }
        }
    }
}