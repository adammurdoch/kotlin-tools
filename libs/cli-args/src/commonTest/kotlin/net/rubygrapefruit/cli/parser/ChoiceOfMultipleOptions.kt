package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ChoiceOfMultipleOptions : AbstractParseTest() {
    @Test
    fun `parses choice with shared options`() {
        val parser = oneOf(
            params(option("a").required(), option("b")) { a, b -> listOf("a:$a", b) },
            params(option("c").required(), option("b")) { c, b -> listOf("c:$c", b) }
        )

        assertEquals(listOf("a:one", "two"), parser.parse("-a", "one", "-b", "two").get())
        assertEquals(listOf("a:one", "two"), parser.parse("-b", "two", "-a", "one").get())
        assertEquals(listOf("a:one", null), parser.parse("-a", "one").get())

        assertEquals(listOf("c:one", "two"), parser.parse("-c", "one", "-b", "two").get())
        assertEquals(listOf("c:one", "two"), parser.parse("-b", "two", "-c", "one").get())
        assertEquals(listOf("c:one", null), parser.parse("-c", "one").get())
    }

    @Test
    fun `reports usage of choice with shared options`() {
        val parser = oneOf(
            params(option("a").required(), option("b")) { _, _ -> fail() },
            params(option("c").required(), option("b")) { _, _ -> fail() }
        )

        parser.usage {
            choice("-a <value> [-b <value>]")
            choice("-c <value> [-b <value>]")
        }
        parser.options {
            option("-a <value>")
            option("-b <value>")
            option("-c <value>")
            option("-b <value>")
        }
        parser.noPositionals()
    }

    @Test
    fun `option must be provided for choice with shared options`() {
        val parser = oneOf(
            params(option("a").required(), option("b")) { _, _ -> fail() },
            params(option("c").required(), option("b")) { _, _ -> fail() }
        )

        parser.fails {
            failure(0, "Option -a not provided")
//            failure(0, "Option -c not provided")
        }
        parser.fails("-b", "b") {
            failure(2, "Option -a not provided")
//            failure(2, "Option -c not provided")
        }
        parser.fails("--unknown") {
            failure(0, "Unexpected option '--unknown'")
            failure(1, "Option -a not provided")
//            failure(1, "Option -c not provided")
        }
    }
}