package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.*

abstract class AbstractParseTest {
    protected class NoParams : Runnable {
        override fun Runnable.Context.run() {
        }
    }

    protected data class OneParam(val value: String?) : Runnable {
        override fun Runnable.Context.run() {
        }
    }

    protected fun Parser<*>.usage(usage: String, summary: String? = null) {
        usage { choice(usage, summary) }
    }

    protected fun Parser<*>.usage(config: UsageSummary.() -> Unit) {
        val usage = usage() as UsageInternal
        DefaultUsageSummary(usage).check(config)
    }

    protected fun Parser<*>.noOptions() {
        options {}
    }

    protected fun Parser<*>.option(usage: String, summary: String? = null) {
        options {
            option(usage, summary)
        }
    }

    protected fun Parser<*>.options(config: Options.() -> Unit) {
        val options = (usage() as UsageInternal).options()
        DefaultOptions(options).check(config)
    }

    protected fun Parser<*>.noPositionals() {
        positionals { }
    }

    protected fun Parser<*>.positional(usage: String, summary: String? = null) {
        positionals {
            positional(usage, summary)
        }
    }

    protected fun Parser<*>.positionals(config: Positionals.() -> Unit) {
        val positionals = (usage() as UsageInternal).positionals()
        DefaultPositionals(positionals).check(config)
    }

    protected fun Parser<*>.edges(config: Edges.() -> Unit) {
        val edges = mutableListOf<ParseEdge>()
        (this as ParserInternal<*>).edges(edges::add)
        DefaultEdges(edges, EdgeContext(this)).check(config)
    }

    protected fun Parser<*>.help(help: String) {
        val action = assertIs<HelpAction>(parse("--help").get())
        val output = BufferingOutput()
        action.run(output)
        assertEquals(help, output.text)
    }

    protected fun Parser<*>.fails(vararg args: String, config: Failures.() -> Unit) {
        val result = parse(*args)
        assertIs<Parser.Failure>(result)
        DefaultFailures(result).check(config)

        val e = assertFailsWith<ArgumentParseException> {
            result.get()
        }
        assertTrue(e.message!!.startsWith("Argument parsing failed"))
    }

    protected interface Failures {
        fun failure(index: Int, message: String)

        fun expectedOption(index: Int, flag: String) {
            failure(index, "Expected option $flag")
        }

        fun expectedPositional(index: Int, name: String) {
            failure(index, "Expected parameter <$name>")
        }

        fun expectedCommand(index: Int, name: String) {
            failure(index, "Expected '$name'")
        }
    }

    @OptIn(ExperimentalKotlinTestApi::class)
    private class DefaultFailures(val failure: Parser.Failure) : Failures {
        private var current = 0

        fun check(config: Failures.() -> Unit) {
            current = 0
            config()
            assertEquals(current, failure.problems.size) { "expected $current problems, found: ${failure.problems.text()}" }
        }

        override fun failure(index: Int, message: String) {
            assertTrue(current < failure.problems.size) { "expected at least ${current + 1} problems, found: ${failure.problems.text()}" }
            val problem = failure.problems[current]
            assertEquals(message, problem.problem.text()) { "unexpected message for problem #$current in ${failure.problems.text()}" }
            assertEquals(index, problem.index) { "unexpected index for problem #$current in ${failure.problems.text()}" }
            current++
        }

        private fun List<ProblemAtIndex>.text(): String {
            return map { "(${it.index} - ${it.problem.text()})" }.toString()
        }
    }

    protected interface UsageSummary {
        fun choice(usage: String, summary: String? = null)
    }

    private class DefaultUsageSummary(val usage: UsageInternal) : UsageSummary {
        private val choices = usage.choices()
        private var current = 0

        fun check(config: UsageSummary.() -> Unit) {
            current = 0
            config()
            assertEquals(current, choices.size)

            if (current == 1) {
                assertEquals(usage.usage(), choices.first().usage())
            } else {
                assertEquals(choices.map { it.usage() }, usage.usage().lines())
            }
        }

        override fun choice(usage: String, summary: String?) {
            val choice = choices[current]
            assertEquals(usage, choice.usage())
            assertEquals(summary, choice.summary())
            current++
        }

        private fun UsageInternal.usage(): String {
            val output = BufferingOutput()
            writeTo(output)
            return output.text
        }

        private fun UsageInternal.summary(): String? {
            val output = BufferingOutput()
            writeSummaryTo(output)
            val text = output.text
            return if (text.isEmpty()) {
                null
            } else {
                text
            }
        }
    }

    protected interface Options {
        fun option(usage: String, summary: String? = null)
    }

    private class DefaultOptions(val options: List<UsageInternal.OptionUsage>) : Options {
        private var current = 0

        fun check(config: Options.() -> Unit) {
            current = 0
            config()
            assertEquals(current, options.size)
        }

        override fun option(usage: String, summary: String?) {
            val option = options[current]

            assertEquals(usage, option.usage())
            assertEquals(summary, option.summary())

            current++
        }

        private fun UsageInternal.OptionUsage.usage(): String {
            val output = BufferingOutput()
            writeOptionUsageTo(output)
            return output.text
        }

        private fun UsageInternal.OptionUsage.summary(): String? {
            val output = BufferingOutput()
            writeOptionSummaryTo(output)
            val text = output.text
            return if (text.isEmpty()) null else text
        }
    }

    protected interface Positionals {
        fun positional(usage: String, summary: String? = null)
    }

    private class DefaultPositionals(val positionals: List<UsageInternal.PositionalUsage>) : Positionals {
        private var current = 0

        fun check(config: Positionals.() -> Unit) {
            current = 0
            config()
            assertEquals(current, positionals.size)
        }

        override fun positional(usage: String, summary: String?) {
            val positional = positionals[current]

            assertEquals(usage, positional.usage())
            assertEquals(summary, positional.summary())

            current++
        }

        private fun UsageInternal.PositionalUsage.usage(): String {
            val output = BufferingOutput()
            writePositionalUsageTo(output)
            return output.text
        }

        private fun UsageInternal.PositionalUsage.summary(): String? {
            val output = BufferingOutput()
            writePositionalSummaryTo(output)
            val text = output.text
            return if (text.isEmpty()) null else text
        }
    }

    protected interface Edges {
        fun nonPositional(config: (MatcherEdge.() -> Unit)? = null)
        fun positional(config: (MatcherEdge.() -> Unit)? = null)
        fun endOfInput(value: Any?, config: (Edge.() -> Unit)? = null)
    }

    @OptIn(ExperimentalKotlinTestApi::class)
    private class DefaultEdges(val edges: List<ParseEdge>, val context: EdgeContext) : Edges {
        private var current = 0

        fun check(config: Edges.() -> Unit) {
            current = 0
            config()
            assertEquals(current, edges.size) {
                "Unexpected edges found in $edges"
            }

            assertTrue(edges.isNotEmpty())
            val notEndOfInput = edges.count { it is ParseEdge.Matcher && it.expecting != null }
            val terminating = edges.count { it is ParseEdge.Terminating }
            assertTrue(terminating > 0 || notEndOfInput > 0) { "expected a terminating edge in $edges" }
        }

        override fun nonPositional(config: (MatcherEdge.() -> Unit)?) {
            val edge = assertIs<ParseEdge.Matcher>(edges[current])
            assertFalse(edge.positional)
            if (config != null) {
                DefaultMatcherEdge(edge, context).check(config)
            }
            current++
        }

        override fun positional(config: (MatcherEdge.() -> Unit)?) {
            val edge = assertIs<ParseEdge.Matcher>(edges[current])
            assertTrue(edge.positional)
            if (config != null) {
                DefaultMatcherEdge(edge, context).check(config)
            }
            current++
        }

        override fun endOfInput(value: Any?, config: (Edge.() -> Unit)?) {
            val edge = assertIs<ParseEdge.EndOfInput>(edges[current])
            assertEquals(value, edge.value())

            assertEquals(value, context.parser.parse(context.previousArgs).get())

            if (config != null) {
                DefaultEndOfInput(edge, context).config()
            }

            current++
        }
    }

    protected interface Edge {
        /**
         * Fails with generic unexpected message
         */
        fun doesNotMatch(firstArg: String, vararg moreArgs: String)

        /**
         * Fails with specific message
         */
        fun doesNotMatch(firstArg: String, vararg moreArgs: String, count: Int = 1, message: String)
    }

    protected interface MatcherEdge : Edge {
        fun matches(firstArg: String, vararg moreArgs: String, config: (Edges.() -> Unit)? = null)

        fun fails(firstArg: String, vararg moreArgs: String, index: Int, message: String, config: (Edge.() -> Unit)? = null)

        fun expectingOption(name: String)
        fun expectingCommand(name: String)
        fun expectingPositional(name: String)
        fun expecting(text: String)
    }

    private class ExpectedProblem(val index: Int, val message: String)

    private class EdgeContext(val parser: Parser<*>, val previousArgs: List<String>, val previousProblems: List<ExpectedProblem>) {
        constructor(parser: Parser<*>) : this(parser, emptyList(), emptyList())

        fun plusArgs(args: List<String>) = EdgeContext(parser, previousArgs + args, previousProblems)
    }

    @OptIn(ExperimentalKotlinTestApi::class)
    private abstract class DefaultEdge : Edge {
        protected fun claimsUnrecognized(parser: Parser<*>, edge: ParseEdge, previousArgs: List<String>, args: List<String>, count: Int, expectedProblems: List<ExpectedProblem>) {
            val allArgs = previousArgs + args
            val args = DefaultInputArgs(previousArgs.size, allArgs)

            val claimedProblem = edge.maybeClaimUnrecognized(args)
            assertNotNull(claimedProblem)
            assertEquals(expectedProblems.last().message, claimedProblem.problem.text())
            assertEquals(count, claimedProblem.count)

            val result = parser.parse(allArgs)
            assertIs<Parser.Failure>(result)

            assertEquals(expectedProblems.size, result.problems.size) { "unexpected problems in ${result.problems}" }
            expectedProblems.forEachIndexed { index, expectedProblem ->
                val parseProblem = result.problems[index]
                assertEquals(expectedProblem.message, parseProblem.problem.text())
                assertEquals(expectedProblem.index, parseProblem.index)

            }
        }
    }

    private class DefaultEndOfInput(val edge: ParseEdge.Terminating, val context: EdgeContext) : DefaultEdge() {
        override fun doesNotMatch(firstArg: String, vararg moreArgs: String) {
            throw IllegalStateException()
        }

        override fun doesNotMatch(firstArg: String, vararg moreArgs: String, count: Int, message: String) {
            val args = listOf(firstArg) + moreArgs
            claimsUnrecognized(context.parser, edge, context.previousArgs, args, count, context.previousProblems + ExpectedProblem(context.previousArgs.size, message))
        }
    }

    @OptIn(ExperimentalKotlinTestApi::class)
    private class DefaultMatcherEdge(val edge: ParseEdge.Matcher, val context: EdgeContext) : DefaultEdge(), MatcherEdge {
        var checkedExpectation = false

        fun check(config: MatcherEdge.() -> Unit) {
            config()
            assertTrue(edge.expecting == null || checkedExpectation) { "edge has an expectation but it was not checked by the test" }
        }

        override fun matches(firstArg: String, vararg moreArgs: String, config: (Edges.() -> Unit)?) {
            val args = listOf(firstArg) + moreArgs
            val transition = next(args)
            assertIs<ParseEdge.Transition.Matched>(transition).apply {
                val edges = mutableListOf<ParseEdge>()
                next.edges(edges::add)
                if (config != null) {
                    DefaultEdges(edges, context.plusArgs(args)).check(config)
                }
                assertEquals(moreArgs.size + 1, count)
            }
        }

        override fun doesNotMatch(firstArg: String, vararg moreArgs: String) {
            checkDoesNotMatch(firstArg, moreArgs, 1, null)
        }

        override fun doesNotMatch(firstArg: String, vararg moreArgs: String, count: Int, message: String) {
            checkDoesNotMatch(firstArg, moreArgs, count, message)
        }

        private fun checkDoesNotMatch(firstArg: String, moreArgs: Array<out String>, count: Int, message: String?) {
            val args = listOf(firstArg) + moreArgs
            val transition = next(args)
            assertIs<ParseEdge.Transition.NotMatched>(transition)

            if (message != null) {
                claimsUnrecognized(context.parser, edge, context.previousArgs, args, count, context.previousProblems + ExpectedProblem(context.previousArgs.size, message))
            }
        }

        override fun fails(firstArg: String, vararg moreArgs: String, index: Int, message: String, config: (Edge.() -> Unit)?) {
            val args = listOf(firstArg) + moreArgs
            val transition = next(args)
            val matched = assertIs<ParseEdge.Transition.Matched>(transition)
            val edges = mutableListOf<ParseEdge>()
            matched.next.edges(edges::add)
            assertEquals(1, edges.size)
            assertIs<ParseEdge.Failed>(edges.first()).apply {
                val problem = failure()
                assertEquals(message, problem.problem.text())
                assertEquals(index, problem.index)

                require(context.previousProblems.isEmpty())
                val expectedProblem = ExpectedProblem(context.previousArgs.size + index, message)

                val result = context.parser.parse(context.previousArgs + args)
                assertIs<Parser.Failure>(result)

                assertEquals(1, result.problems.size)
                val parseProblem = result.problems.first()
                assertEquals(message, parseProblem.problem.text())
                assertEquals(expectedProblem.index, parseProblem.index)

                if (config != null) {
                    DefaultEndOfInput(this, EdgeContext(context.parser, context.previousArgs + args, listOf(expectedProblem))).config()
                }
            }
        }

        private fun next(args: List<String>): ParseEdge.Transition {
            return edge.next(DefaultInputArgs(0, args))
        }

        override fun expectingOption(name: String) {
            expecting("option $name")
        }

        override fun expectingPositional(name: String) {
            expecting("parameter <$name>")
        }

        override fun expectingCommand(name: String) {
            expecting("'$name'")
        }

        override fun expecting(text: String) {
            assertEquals(text, edge.expecting)
            checkedExpectation = true
        }
    }
}

internal fun Problem.text(): String {
    val buffer = BufferingOutput()
    writeTo(buffer)
    return buffer.text
}
