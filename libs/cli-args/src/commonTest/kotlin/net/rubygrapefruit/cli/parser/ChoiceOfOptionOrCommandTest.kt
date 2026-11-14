package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ChoiceOfOptionOrCommandTest : AbstractParseTest() {
    @Test
    fun `parses choice of mapped option and command`() {
        val parser = oneOf(
            params(option("help", "h").boolean().required()) { "help" },
            params(command("run")) { "run" }
        )

        assertEquals("help", parser.parse("-h").get())
        assertEquals("help", parser.parse("--help").get())
        assertEquals("run", parser.parse("run").get())
    }

    @Test
    fun `reports usage of choice of mapped option and command`() {
        val parser = oneOf(
            params(option("help", "h").boolean().required()) { fail() },
            params(command("run")) { fail() }
        )

        parser.usage {
            choice("-h | --help")
            choice("run")
        }
        parser.options {
            option("-h, --help")
        }
    }

    @Test
    fun `either option or command must be provided`() {
        val parser = oneOf(
            params(option("help", "h").boolean().required()) { fail() },
            params(command("run")) { fail() }
        )

        parser.fails {
            failure(0, "Expected 'run'")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'. Expected 'run' instead")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Expected 'run'")
        }

        // Both

        parser.fails("--help", "run") {
//            failure(1, "Cannot use 'run' with option '--help'")
            failure(1, "Unexpected argument 'run'")
        }
        parser.fails("run", "--help") {
//            failure(1, "Cannot use '--help' with 'run'")
            failure(1, "Unexpected option '--help'")
        }

        parser.fails("unknown", "run", "--help") {
            failure(0, "Unexpected argument 'unknown'")
//            failure(2, "Cannot use '--help' with 'run'")
            failure(2, "Unexpected option '--help'")
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = oneOf(
            params(option("help", "h").boolean().required()) { fail() },
            params(command("run")) { fail() }
        )

        parser.fails("run", "unknown") {
            failure(1, "Unexpected argument 'unknown'")
        }
        parser.fails("run", "--unknown") {
            failure(1, "Unexpected option '--unknown'")
        }
        parser.fails("unknown", "run") {
            failure(0, "Unexpected argument 'unknown'")
        }
        parser.fails("--help", "unknown") {
            failure(1, "Unexpected argument 'unknown'")
        }
        parser.fails("--help", "--unknown") {
            failure(1, "Unexpected option '--unknown'")
        }
        parser.fails("unknown", "--help", "unknown") {
            failure(0, "Unexpected argument 'unknown'")
            failure(2, "Unexpected argument 'unknown'")
        }
    }

    @Test
    fun `option or command cannot appear twice`() {
        val parser = oneOf(
            params(option("help", "h").boolean().required()) { fail() },
            params(command("run")) { fail() }
        )

        parser.fails("run", "run") {
            failure(1, "Unexpected argument 'run'")
        }
        parser.fails("--help", "--help") {
            failure(1, "Option --help can only appear once")
        }
    }
}