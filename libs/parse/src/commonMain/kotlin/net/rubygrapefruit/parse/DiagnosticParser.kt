package net.rubygrapefruit.parse

internal class DiagnosticParser<IN, OUT> private constructor(
    private val parser: Parser<IN, OUT>,
    private val logger: Logger,
    private val listener: Listener
) : Parser<IN, OUT>, CombinatorBuilder<OUT> {
    override fun <IN : Input<*>> compile(compiler: CombinatorBuilder.Compiler<IN>): CompiledParser<IN, OUT> {
        val parser = when (parser) {
            is CombinatorBuilder<*> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as CombinatorBuilder<OUT>).compile(DiagnosticCompiler(compiler, logger))
            }

            is TypedInputCombinatorBuilder<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                (parser as TypedInputCombinatorBuilder<IN, OUT>).compile(DiagnosticCompiler(compiler, logger))
            }

            else -> {
                compiler.compile(parser)
            }
        }
        return DiagnosticCompiledParser(parser, logger, listener)
    }

    companion object {
        private var nextId = 0

        private object NoOpListener : Listener {
            override fun requireMore(advance: Int, commit: Int) {
            }
        }

        fun <IN, OUT> of(parser: Parser<IN, OUT>, log: Boolean, listener: Listener = NoOpListener): Parser<IN, OUT> {
            val logger = if (log) Logger.Active() else Logger.Disabled
            return DiagnosticParser(parser, logger, listener)
        }
    }

    interface Listener {
        fun requireMore(advance: Int, commit: Int)
    }

    private class DiagnosticCompiler<IN>(private val compiler: CombinatorBuilder.Compiler<IN>, private val logger: Logger) : CombinatorBuilder.Compiler<IN> {
        override fun <OUT> compile(parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
            return compiler.compile(DiagnosticParser(parser, logger, NoOpListener))
        }

        override fun <OUT> compileRecursive(outer: Parser<*, OUT>, compiledOuter: CompiledParser<IN, OUT>, parser: Parser<*, OUT>): CompiledParser<IN, OUT> {
            return compiler.compileRecursive(outer, compiledOuter, parser)
        }

        override fun compileWithNoResult(parser: Parser<*, *>): CompiledParser<IN, Unit> {
            if (parser is DiscardableParser<*>) {
                @Suppress("UNCHECKED_CAST")
                val noResult = (parser as DiscardableParser<IN>).withNoResult()
                return compiler.compile(DiagnosticParser(noResult, logger, NoOpListener))
            } else {
                return DiagnosticCompiledParser(compiler.compileWithNoResult(parser), logger, NoOpListener)
            }
        }

        override fun maybeAsSingleInputParser(parser: Parser<*, *>): SingleInputParser<IN>? {
            return compiler.maybeAsSingleInputParser(parser)
        }
    }

    private class DiagnosticCompiledParser<IN, OUT>(
        private val parser: CompiledParser<IN, OUT>,
        private val logger: Logger,
        private val listener: Listener
    ) : CompiledParser<IN, OUT> {
        override fun toString(): String {
            return "{d $parser}"
        }

        override fun <NEXT> start(next: ParseContinuation<IN, OUT, NEXT>): PullParser<IN, NEXT> {
            return DiagnosticPullParser(parser.start(next), logger, listener)
        }
    }

    private class DiagnosticPullParser<IN, OUT>(
        private val parser: PullParser<IN, OUT>,
        private val logger: Logger,
        private val listener: Listener
    ) : PullParser<IN, OUT> {
        private val id = nextId++

        override fun toString(): String {
            return "{d $parser}"
        }

        override fun stop(): PullParser.Failed {
            return parser.stop()
        }

        override fun parse(input: IN, max: Int): PullParser.Result<IN, OUT> {
            if (parser is DiagnosticPullParser) {
                return parser.parse(input, max)
            }

            val parserState = parser.toString()
            logger.logTopLevel("[$id] start (max=$max) $parserState")
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
                    require(result.advance <= max) { "$parserState returned $result when max=$max" }
                    listener.requireMore(result.advance, result.commit)
                    val effective = when (result.parser) {
                        parser -> this
                        else -> DiagnosticPullParser(result.parser, logger, listener)
                    }
                    PullParser.RequireMore(result.advance, result.commit, result.matched, effective, result.failedChoice)
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