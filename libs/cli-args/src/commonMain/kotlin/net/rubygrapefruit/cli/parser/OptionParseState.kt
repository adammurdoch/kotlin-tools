package net.rubygrapefruit.cli.parser

internal abstract class OptionParseState<T>(protected val flags: List<String>, protected val names: List<String>) : ParseState<T> {
    private var seen: String? = null
    protected var suggested = false
        private set

    override fun maybeParse(args: InputArgs): ParseState.PartialResult<T> {
        val first = args.first()
        if (!flags.contains(first)) {
            return ParseState.NotRecognized
        }
        seen = first
        return matched(args)
    }

    override fun claimUnrecognized(args: InputArgs): ParseState.Failure? {
        val first = args.first()
        if (flags.contains(first)) {
            val count = consumeBadlyFormed(args)
            val problem = if (first == seen) {
                RepeatedOption(first)
            } else {
                RepeatedOption(first, seen!!)
            }
            return ParseState.Failure(count, ProblemAtIndex(args.index, problem))
        } else if (args.firstIsOption()) {
            names.forEachIndexed { index, name ->
                if (first.equals("-$name", true) || first.equals("--$name", true)) {
                    val count = consumeBadlyFormed(args)
                    suggested = true
                    return ParseState.Failure(count, ProblemAtIndex(args.index, UnexpectedOption(first, flags[index])))
                }
            }
        }
        return null
    }

    protected abstract fun matched(args: InputArgs): ParseState.Finished<T>

    /**
     * Calculate how many inputs to consume for badly formed option.
     */
    protected abstract fun consumeBadlyFormed(args: InputArgs): Int
}