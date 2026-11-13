package net.rubygrapefruit.cli.parser

internal abstract class OptionParseState<T>(protected val flags: List<String>, protected val names: List<String>) : ParseState<T> {
    private var result: Finished<T>? = null
    private var seen: String? = null

    override val expectingOption: Boolean
        get() = true

    override val finished: Boolean
        get() = result != null

    override fun maybeParse(args: InputArgs): ParseState.PartialResult {
        val first = args.first()
        if (!flags.contains(first)) {
            return ParseState.NotRecognized
        }
        seen = first
        val result = matched(args)
        this.result = result
        return ParseState.Recognized(result.count)
    }

    override fun finish(index: Int): ParsedArguments<T> {
        val result = result ?: endOfArguments(index)
        return when (result) {
            is Finished.Success -> Success(result)
            is Finished.Failure -> Failure(result)
            is Finished.MissingOption -> MissingOption(index)
        }
    }

    protected abstract fun matched(args: InputArgs): Finished<T>

    protected abstract fun endOfArguments(index: Int): Finished<T>

    /**
     * Calculate how many inputs to consume for badly formed option.
     */
    protected abstract fun consumeBadlyFormed(args: InputArgs): Int

    protected sealed interface Finished<out T> {
        val count: Int

        class Failure(override val count: Int, val problem: ProblemAtIndex) : Finished<Nothing>
        class Success<T>(override val count: Int, val value: T) : Finished<T>

        object MissingOption : Finished<Nothing> {
            override val count: Int
                get() = 0
        }
    }

    private abstract inner class Result {
        protected var suggested = false
            private set

        protected fun maybeClaimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            val first = args.first()
            if (seen != null && flags.contains(first)) {
                val count = consumeBadlyFormed(args)
                val problem = if (first == seen) {
                    RepeatedOption(first)
                } else {
                    RepeatedOption(first, seen!!)
                }
                return ParsedArguments.Unrecognized(count, ProblemAtIndex(args.index, problem))
            } else if (args.firstIsOption()) {
                names.forEachIndexed { index, name ->
                    if (first.equals("-$name", true) || first.equals("--$name", true)) {
                        val count = consumeBadlyFormed(args)
                        suggested = true
                        return ParsedArguments.Unrecognized(count, ProblemAtIndex(args.index, UnexpectedOption(first, flags[index])))
                    }
                }
            }
            return null
        }
    }

    private inner class Success<T>(val result: Finished.Success<T>) : Result(), ParsedArguments.Success<T> {
        override fun value(): T {
            return result.value
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return maybeClaimUnrecognized(args)
        }
    }

    private inner class Failure(val result: Finished.Failure) : Result(), ParsedArguments.Failure {
        override fun problems(): List<ProblemAtIndex> {
            return listOf(result.problem)
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return maybeClaimUnrecognized(args)
        }
    }

    private inner class MissingOption(val index: Int) : Result(), ParsedArguments.Failure {
        override fun problems(): List<ProblemAtIndex> {
            return if (suggested) {
                emptyList()
            } else {
                val longest = flags.maxBy { it.length }
                listOf(ProblemAtIndex(index, MissingOption(longest)))
            }
        }

        override fun claimUnrecognized(args: InputArgs): ParsedArguments.Unrecognized? {
            return maybeClaimUnrecognized(args)
        }
    }
}