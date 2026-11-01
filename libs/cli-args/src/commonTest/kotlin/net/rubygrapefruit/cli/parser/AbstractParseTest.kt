package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.ArgParseException
import kotlin.test.*

abstract class AbstractParseTest {
    protected fun Parser<*>.fails(vararg args: String, config: Failures.() -> Unit) {
        val result = parse(*args)
        assertIs<Parser.Failure>(result)
        DefaultFailures(result).check(config)

        assertFailsWith<ArgParseException> {
            result.get()
        }
    }

    protected interface Failures {
        fun failure(index: Int, message: String)
    }

    @OptIn(ExperimentalKotlinTestApi::class)
    private class DefaultFailures(val failure: Parser.Failure) : Failures {
        private var current = 0

        fun check(config: Failures.() -> Unit) {
            config()
            assertEquals(current, failure.problems.size) { "expected $current problems, found: ${failure.problems}"}
        }

        override fun failure(index: Int, message: String) {
            assertTrue(current < failure.problems.size) { "expected at least ${current + 1} problems, found: ${failure.problems}" }
            val problem = failure.problems[current]
            assertEquals(index, problem.index) { "unexpected index for problem, expected $index, found $problem in ${failure.problems}" }
            assertEquals(message, problem.message)
            current++
        }
    }
}