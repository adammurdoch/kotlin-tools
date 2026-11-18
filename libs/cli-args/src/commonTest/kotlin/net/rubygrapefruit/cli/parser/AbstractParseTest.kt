package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.*

abstract class AbstractParseTest {
    protected class NoParams : Runnable {
        override fun Runnable.Context.run() {
        }
    }

    protected class OneParam(val value: String?) : Runnable {
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
        (this as ParserInternal<*>).edges(edges)
        DefaultEdges(edges, this, emptyList()).check(config)
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

        private fun Problem.text(): String {
            val buffer = BufferingOutput()
            writeTo(buffer)
            return buffer.text
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
        fun nonPositional(config: MatcherEdge.() -> Unit = {})
        fun positional(config: MatcherEdge.() -> Unit = {})
        fun endOfInput(value: Any?, config: Edge.() -> Unit = {})
        fun aborts(message: String)
    }

    @OptIn(ExperimentalKotlinTestApi::class)
    private class DefaultEdges(val edges: List<ParseEdge>, val parser: Parser<*>, val previousArgs: List<String>) : Edges {
        private var current = 0

        fun check(config: Edges.() -> Unit) {
            current = 0
            config()
            assertEquals(current, edges.size) {
                "Unexpected edges found in $edges"
            }

            assertTrue(edges.isNotEmpty())
            val required = edges.count { it is ParseEdge.NotEndOfInput }
            val endOfInput = edges.count { it is ParseEdge.EndOfInput }
            assertTrue(required > 0 || endOfInput > 0) { "expected a required or end-of-input edge in $edges" }
        }

        override fun nonPositional(config: MatcherEdge.() -> Unit) {
            val edge = assertIs<ParseEdge.NonPositional>(edges[current])
            DefaultMatcherEdge(edge, parser, previousArgs).config()
            current++
        }

        override fun positional(config: MatcherEdge.() -> Unit) {
            val edge = assertIs<ParseEdge.Positional>(edges[current])
            DefaultMatcherEdge(edge, parser, previousArgs).config()
            current++
        }

        override fun endOfInput(value: Any?, config: Edge.() -> Unit) {
            val edge = assertIs<ParseEdge.EndOfInput>(edges[current])
            assertEquals(value, edge.value())
            assertEquals(value, parser.parse(previousArgs).get())
            DefaultEndOfInput(edge, previousArgs).config()
            current++
        }

        override fun aborts(message: String) {
            val edge = assertIs<ParseEdge.NotEndOfInput>(edges[current])
            val buffer = BufferingOutput()
            edge.abort().writeTo(buffer)
            assertEquals(message, buffer.text)
            current++
        }
    }

    protected interface Edge {
        fun doesNotMatch(firstArg: String, vararg moreArgs: String, message: String? = null)
    }

    protected interface MatcherEdge : Edge {
        fun matches(firstArg: String, vararg moreArgs: String, config: Edges.() -> Unit)

        fun fails(firstArg: String, vararg moreArgs: String, index: Int, message: String)
    }

    private class DefaultEndOfInput(val edge: ParseEdge.EndOfInput, val previousArgs: List<String>) : Edge {
        override fun doesNotMatch(firstArg: String, vararg moreArgs: String, message: String?) {
            val allArgs = buildList {
                addAll(previousArgs)
                add(firstArg)
                addAll(moreArgs)
            }
            val recognized = BooleanArray(allArgs.size)
            val args = DefaultInputArgs(previousArgs.size, allArgs, recognized)

            val problem = edge.maybeClaimUnrecognized(args)

            assertNotNull(problem)
            val buffer = BufferingOutput()
            problem.writeTo(buffer)
            assertEquals(message, buffer.text)
        }
    }

    private class DefaultMatcherEdge(val edge: ParseEdge.Matcher, val parser: Parser<*>, val previousArgs: List<String>) : MatcherEdge {
        override fun matches(firstArg: String, vararg moreArgs: String, config: Edges.() -> Unit) {
            val args = listOf(firstArg) + moreArgs
            val transition = next(args)
            assertIs<ParseEdge.Transition.Matched>(transition).apply {
                val edges = mutableListOf<ParseEdge>()
                next.edges(edges)
                DefaultEdges(edges, parser, previousArgs + args).check(config)
                assertEquals(moreArgs.size + 1, count)
            }
        }

        override fun doesNotMatch(firstArg: String, vararg moreArgs: String, message: String?) {
            val transition = next(firstArg, moreArgs)
            assertIs<ParseEdge.Transition.NotMatched>(transition)

            if (message != null) {
                TODO()
            }
        }

        override fun fails(firstArg: String, vararg moreArgs: String, index: Int, message: String) {
            val transition = next(firstArg, moreArgs)
            assertIs<ParseEdge.Transition.Failed>(transition).apply {
                val buffer = BufferingOutput()
                problem.problem.writeTo(buffer)
                assertEquals(message, buffer.text)
                assertEquals(index, problem.index)
            }
        }

        private fun next(firstArg: String, moreArgs: Array<out String>): ParseEdge.Transition {
            return next(listOf(firstArg) + moreArgs)
        }

        private fun next(args: List<String>): ParseEdge.Transition {
            return edge.next(DefaultInputArgs(0, args, BooleanArray(args.size)))
        }
    }
}