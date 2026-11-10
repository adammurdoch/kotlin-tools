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

    protected val Parser<*>.usage: String
        get() {
            val output = BufferingOutput()
            usage().writeTo(output)
            return output.text
        }

    protected val Parser<*>.optionUsage: String
        get() {
            val options = (usage() as UsageInternal).options()
            assertEquals(1, options.size)
            val output = BufferingOutput()
            options.first().writeOptionUsageTo(output)
            return output.text
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
}