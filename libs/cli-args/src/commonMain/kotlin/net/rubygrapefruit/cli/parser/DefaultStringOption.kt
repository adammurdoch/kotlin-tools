package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.Output

internal class DefaultStringOption(unsortedNames: List<String>) : AbstractParser<String?>(), StringOption {
    private val names = unsortedNames.sortedWith { a, b ->
        val diff = a.length.compareTo(b.length)
        if (diff != 0) {
            diff
        } else {
            a.compareTo(b)
        }
    }
    private val flags = names.map { name -> if (name.length == 1) "-$name" else "--$name" }

    override fun boolean(): BooleanOption {
        return DefaultBooleanOption(flags, names)
    }

    override fun start(): ParseState<String?> {
        return State(flags, names)
    }

    override fun usage(): UsageInternal {
        return OptionUsage()
    }

    private inner class OptionUsage : UsageInternal {
        override fun writeTo(output: Output) {
            output.write("[")
            flags.forEachIndexed { index, flag ->
                if (index > 0) {
                    output.write(" | ")
                }
                output.write("$flag <value>")
            }
            output.write("]")
        }
    }

    private class State(private val flags: List<String>, private val names: List<String>) : ParseState<String?> {
        val flag = flags.first()

        private var seen: String? = null

        override fun maybeParse(args: InputArgs): ParseState.PartialResult<String?> {
            val first = args.first()
            if (seen != null || !flags.contains(first)) {
                return ParseState.NotRecognized
            }
            seen = first
            return if (!args.hasSecond() || args.secondIsOption()) {
                ParseState.Failure(1, ProblemAtIndex(args.index + 1, MissingOptionValue(first)))
            } else {
                val value = args.second()
                ParseState.Finished(2, ParseState.FixedResult(value))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
            val first = args.first()
            if (flags.contains(first)) {
                val count = if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
                val problem = if (first == seen) {
                    RepeatedOption(first)
                } else {
                    RepeatedOption(first, seen!!)
                }
                return ParseState.Failure(count, ProblemAtIndex(args.index, problem))
            } else if (args.firstIsOption()) {
                names.forEachIndexed { index, name ->
                    if (first.equals("-$name", true) || first.equals("--$name", true)) {
                        val count = if (args.hasSecond() && !args.secondIsOption() && !args.secondIsRecognized()) 2 else 1
                        return ParseState.Failure(count, ProblemAtIndex(args.index, UnexpectedOption(first, flags[index])))
                    }
                }
            }
            return null
        }

        override fun finish(index: Int): ParseState.Result<String?> {
            return ParseState.FixedResult(null)
        }
    }
}