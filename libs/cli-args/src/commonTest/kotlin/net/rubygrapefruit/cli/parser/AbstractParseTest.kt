package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.Runnable
import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.*

abstract class AbstractParseTest {
    protected class NoParams : Runnable {
        override fun run() {
        }
    }

    protected class OneParam(val value: String?) : Runnable {
        override fun run() {
        }
    }

    protected fun Parser<*>.usage(usage: String) {
        usage { choice(usage) }
    }

    protected fun Parser<*>.usage(config: UsageSummary.() -> Unit) {
        val usage = usage() as UsageInternal
        DefaultUsageSummary(usage).check(config)
    }

    protected fun Parser<*>.noOptions() {
        options {}
    }

    protected fun Parser<*>.option(usage: String) {
        options {
            option(usage)
        }
    }

    protected fun Parser<*>.options(config: Options.() -> Unit) {
        val options = (usage() as UsageInternal).options()
        DefaultOptions(options).check(config)
    }

    protected fun Parser<*>.fails(vararg args: String, config: Failures.() -> Unit) {
        val result = parse(*args)
        assertIs<Parser.Failure>(result)
        DefaultFailures(result).check(config)

        val e = assertFailsWith<ArgumentParseException> {
            result.get()
        }
        assertEquals("Argument parsing failed", e.message)
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
        fun choice(usage: String)
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

        override fun choice(usage: String) {
            val choice = choices[current]
            assertEquals(usage, choice.usage())
            current++
        }

        private fun UsageInternal.usage(): String {
            val output = BufferingOutput()
            writeTo(output)
            return output.text
        }
    }

    protected interface Options {
        fun option(usage: String)
    }

    private class DefaultOptions(val options: List<UsageInternal.OptionUsage>) : Options {
        private var current = 0

        fun check(config: Options.() -> Unit) {
            current = 0
            config()
            assertEquals(current, options.size)
        }

        override fun option(usage: String) {
            val option = options[current]
            val output = BufferingOutput()
            option.writeOptionUsageTo(output)

            assertEquals(usage, output.text)
            current++
        }
    }
}