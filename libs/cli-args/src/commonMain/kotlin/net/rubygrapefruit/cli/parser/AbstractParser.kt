package net.rubygrapefruit.cli.parser

internal abstract class AbstractParser<out T> : Parser<T>, ParserInternal<T> {
    protected open fun startAll(): List<ParseState<T>> {
        return listOf(start())
    }

    override fun parse(args: List<String>): Parser.Result<T> {
        val results = parseAlternatives(args, startAll().map { Alternative(it, BooleanArray(args.size)) })
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
                val problems = mutableListOf<ProblemAtIndex>()
                var seen = false
                for (index in args.indices) {
                    val arg = args[index]
                    if (arg.startsWith("-")) {
                        problems.add(ProblemAtIndex(index, UnexpectedOption(arg)))
                    } else if (!seen) {
                        seen = true
                        problems.add(ProblemAtIndex(index, UnexpectedArgument(arg, names)))
                    } else {
                        problems.add(ProblemAtIndex(index, UnexpectedArgument(arg)))
                    }
                }
                if (!seen) {
                    problems.add(ProblemAtIndex(args.size, MissingCommands(names)))
                }
                return ParserInternal.Failure(problems, this)
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

    private fun parseAlternatives(args: List<String>, alteratives: List<Alternative<T>>): List<Result<T>> {
        var index = 0
        var remaining = alteratives.toMutableList()
        while (index < args.size) {
            val nextAlternatives = mutableListOf<Alternative<T>>()
            for (alternative in remaining) {
                val state = alternative.state
                if (alternative.advance > 0) {
                    nextAlternatives.add(alternative)
                } else if (state.expectingOption || state.expectingPositional) {
                    val recognized = alternative.recognized
                    val current = DefaultInputArgs(index, args, recognized)
                    val parseResult = state.maybeParse(current)
                    when (parseResult) {
                        is ParseState.Recognized -> {
                            require(parseResult.count > 0)
                            repeat(parseResult.count) {
                                recognized[index + it] = true
                            }
                            alternative.advance = parseResult.count
                            nextAlternatives.add(alternative)
                        }

                        is ParseState.NotRecognized -> {
                            // Attempt to recover and continue
                            alternative.advance = 1
                        }
                    }
                }
            }
            if (nextAlternatives.isNotEmpty()) {
                remaining = nextAlternatives
            }
            val advance = remaining.minOf { it.advance }
            for (alternative in remaining) {
                alternative.advance -= advance
            }
            if (advance == 0) {
                // All alternatives are finished
                break
            }
            index += advance
        }

        return remaining.map { Result(it.state.finish(args.size), it.recognized) }
    }

    private class Alternative<T>(val state: ParseState<T>, val recognized: BooleanArray) {
        var advance = 0
    }

    private class Result<T>(val result: ParsedArguments<T>, val recognized: BooleanArray) {
        val recognizedCount = recognized.count { it }
    }
}