package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    protected open fun startAll(): List<ParseState<T>> {
        return listOf(start())
    }

    override fun parse(args: List<String>): Parser.Result<T> {
        val results = startAll().map { parseAlternative(args, it) }
        for (alternative in results) {
            if (alternative.result is ParsedArguments.Success && alternative.recognizedCount == args.size) {
                return ParserInternal.Success(alternative.result.value())
            }
        }

        if (results.size > 1 && results.all { it.recognizedCount == 0 }) {
            val problems = results.flatMap {
                if (it.result is ParsedArguments.Failure) {
                    it.result.problems().mapNotNull { it.problem as? MissingCommand }
                } else {
                    emptyList()
                }
            }
            if (problems.isNotEmpty()) {
                val names = problems.map { it.name }
                for (index in args.indices) {
                    val arg = args[index]
                    if (!arg.startsWith("-")) {
                        return ParserInternal.Failure(listOf(ProblemAtIndex(index, UnexpectedArgument(arg, names))), this)
                    }
                }
                return ParserInternal.Failure(listOf(ProblemAtIndex(args.size, MissingCommands(names))), this)
            }
        }

        val best = results.maxBy { it.recognizedCount }
        val recognized = best.recognized
        val result = best.result

        val unrecognizedProblems = mutableListOf<ProblemAtIndex>()
        var index = 0
        while (index < args.size) {
            if (recognized[index]) {
                index++
                continue
            }

            val current = DefaultInputArgs(index, args, recognized)
            val failure = result.claimUnrecognized(current)
            if (failure != null) {
                require(failure.count > 0)
                unrecognizedProblems.add(failure.problem)
                index += failure.count
            } else {
                val problem = if (current.firstIsOption()) {
                    UnexpectedOption(current.first())
                } else {
                    UnexpectedArgument(current.first())
                }
                unrecognizedProblems.add(ProblemAtIndex(index, problem))
                index++
            }
        }

        return when (result) {
            is ParsedArguments.Failure -> {
                ParserInternal.Failure(result.problems().plus(unrecognizedProblems), this)
            }

            is ParsedArguments.Success -> {
                ParserInternal.Failure(unrecognizedProblems, this)
            }
        }
    }

    private fun parseAlternative(args: List<String>, state: ParseState<T>): Alternative<T> {
        val recognized = BooleanArray(args.size)
        var index = 0
        while (index < args.size && !state.finished) {
            val current = DefaultInputArgs(index, args, recognized)
            val parseResult = state.maybeParse(current)
            when (parseResult) {
                is ParseState.Recognized -> {
                    require(parseResult.count > 0)
                    repeat(parseResult.count) {
                        recognized[index] = true
                        index++
                    }
                }

                is ParseState.NotRecognized -> {
                    // Attempt to recover and continue
                    index++
                }
            }
        }

        val result = state.finish(args.size)

        return Alternative(result, recognized)
    }

    private class Alternative<T>(val result: ParsedArguments<T>, val recognized: BooleanArray) {
        val recognizedCount = recognized.count { it }
    }
}