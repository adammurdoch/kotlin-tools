package net.rubygrapefruit.parse

internal class DiagnosticParser<IN, OUT> private constructor(private val parser: Parser<IN, OUT>, private val logger: Logger) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    constructor(parser: Parser<IN, OUT>, log: Boolean) : this(parser, if (log) Logger.Active(0) else Logger.Disabled)

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
    }

    private class DiagnosticCompiledParser<IN, OUT>(private val parser: CompiledParser<IN, OUT>, private val logger: Logger) : CompiledParser<IN, OUT> {
        override val mayNotAdvanceOnMatch: Boolean
            get() = parser.mayNotAdvanceOnMatch

        override val expectation: Expectation
            get() = parser.expectation

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return DiagnosticPullParser(parser.start(next), logger)
        }
    }

    private class DiagnosticPullParser<IN, OUT>(private val parser: PullParser<IN, OUT>, private val logger: Logger) : PullParser<IN, OUT> {
        override val expectation: Expectation
            get() = parser.expectation

        override fun toString(): String {
            return parser.toString()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            val result = parser.parse(input, max)
            logger.log("-> $parser (max=$max) => $result")
            return when (result) {
                is PullParser.Matched -> {
                    require(result.count <= max)
                    result
                }
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