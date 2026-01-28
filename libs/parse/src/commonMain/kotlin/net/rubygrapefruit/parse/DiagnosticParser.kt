package net.rubygrapefruit.parse

import net.rubygrapefruit.parse.combinators.DiscardParser

internal class DiagnosticParser<IN, OUT> private constructor(
    private val parser: Parser<IN, OUT>,
    private val logger: Logger
) : Parser<IN, OUT>, CombinatorBuilder<OUT>, DiscardableParser<IN> {
    constructor(parser: Parser<IN, OUT>, log: Boolean) : this(parser, if (log) Logger.Active() else Logger.Disabled)

    override fun withNoResult(): Parser<IN, Unit> {
        return DiscardParser(parser)
    }

    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val parser = if (parser is CombinatorBuilder<*>) {
            @Suppress("UNCHECKED_CAST")
            (parser as CombinatorBuilder<OUT>).compile(DiagnosticCompiler(compiler, logger))
        } else {
            compiler.compile(parser)
        }
        return DiagnosticCompiledParser(parser, logger)
    }

    companion object {
        private var nextId = 0
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
        private val id = nextId++

        override fun toString(): String {
            return parser.toString()
        }

        override fun stop(): PullParser.Failed {
            return parser.stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            if (parser is DiagnosticPullParser) {
                return parser.parse(input, max)
            }

            val parserState = parser.toString()
            logger.logTopLevel("[$id] start (max=$max)")
            val result = logger.nested {
                parser.parse(input, max)
            }
            logger.log("[$id] $parserState => $result")

            return when (result) {
                is PullParser.Matched -> result

                is PullParser.Failed -> {
                    require(result.index <= max)
                    result
                }

                is PullParser.RequireMore -> {
                    require(result.advance <= max)
                    PullParser.RequireMore(result.advance, DiagnosticPullParser(result.parser, logger), result.failedChoice)
                }
            }
        }
    }

    private sealed class Logger {
        abstract fun log(message: String)

        abstract fun logTopLevel(message: String)

        abstract fun <T> nested(action: () -> T): T

        class Active : Logger() {
            private var depth: Int = 0

            override fun <T> nested(action: () -> T): T {
                depth++
                try {
                    return action()
                } finally {
                    depth--
                }
            }

            override fun logTopLevel(message: String) {
                if (depth == 0) {
                    log(message)
                }
            }

            override fun log(message: String) {
                repeat(depth) {
                    print("  ")
                }
                println(message)
            }
        }

        data object Disabled : Logger() {
            override fun <T> nested(action: () -> T): T {
                return action()
            }

            override fun logTopLevel(message: String) {
            }

            override fun log(message: String) {
            }
        }
    }
}