package net.rubygrapefruit.cli.parser

import net.rubygrapefruit.cli.ArgParseException
import kotlin.test.ExperimentalKotlinTestApi
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

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
            assertEquals(failure.problems.size, current)
        }

        override fun failure(index: Int, message: String) {
            val problem = failure.problems[current]
            assertEquals(index, problem.index) { "unexpected index for problem - $message" }
            assertEquals(message, problem.message)
            current++
        }
    }
}