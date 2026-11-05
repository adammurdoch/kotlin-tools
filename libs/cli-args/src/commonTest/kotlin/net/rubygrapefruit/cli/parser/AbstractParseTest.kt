package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.terminal.BufferingOutput
import kotlin.test.*

abstract class AbstractParseTest {
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
            assertEquals(message, problem.problem.text()) { "unexpected message for problem #$current, found: ${failure.problems.text()}" }
            assertEquals(index, problem.index) { "unexpected index for problem #$current in ${failure.problems.text()}" }
            current++
        }

        private fun List<ProblemAtIndex>.text(): String {
            return joinToString { it.problem.text() }
        }

        private fun ProblemAtIndex.text() = problem.text()

        private fun Problem.text(): String {
            val buffer = BufferingOutput()
            writeTo(buffer)
            return buffer.text
        }
    }
}