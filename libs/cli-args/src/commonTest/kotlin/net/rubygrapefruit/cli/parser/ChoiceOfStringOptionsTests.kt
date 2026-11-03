package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfStringOptionsTests : AbstractParseTest() {
    @Test
    fun `parses one of string options`() {
        val parser = oneOf(
            option("a"),
            option("b"),
            option("c")
        )

        assertEquals("one", parser.parse("-a", "one").get())
        assertEquals("two", parser.parse("-b", "two").get())
        assertEquals("three", parser.parse("-c", "three").get())

        assertEquals(null, parser.parse().get())

        parser.fails("-a", "one", "-c", "three") {
//            failure(2, "Option -c cannot be used with option -a")
            failure(2, "Unexpected option '-c'")
            failure(3, "Unexpected argument 'three'")
        }
        parser.fails("-a", "one", "-b", "two", "-c", "three") {
//            failure(2, "Option -b cannot be used with option -a")
//            failure(4, "Option -c cannot be used with option -a")
            failure(2, "Unexpected option '-b'")
            failure(3, "Unexpected argument 'two'")
            failure(4, "Unexpected option '-c'")
            failure(5, "Unexpected argument 'three'")
        }
        parser.fails("-c", "three", "-b", "two", "-a", "one") {
//            failure(2, "Option -b cannot be used with option -c")
//            failure(4, "Option -a cannot be used with option -c")
            failure(2, "Unexpected option '-b'")
            failure(3, "Unexpected argument 'two'")
            failure(4, "Unexpected option '-a'")
            failure(5, "Unexpected argument 'one'")
        }
        parser.fails("-a", "-c", "three") {
//            failure(1, "No value provided for option -a")
//            failure(1, "Option -c cannot be used with option -a")
            failure(1, "No value provided for option '-a'")
            failure(2, "Unexpected option '-c'")
            failure(2, "Unexpected argument 'three'")
        }
    }

    @Test
    fun `cannot include other input values`() {
        val parser = oneOf(
            option("a"),
            option("b"),
            option("c")
        )

        parser.fails("-unknown") {
            failure(0, "Unexpected option '-unknown'")
        }
        parser.fails("unknown") {
            failure(0, "Unexpected argument 'unknown'")
        }
    }

    @Test
    fun `each option must have a value`() {
        val parser = oneOf(
            option("a"),
            option("b"),
            option("c")
        )

        parser.fails("-a") {
            failure(1, "No value provided for option -a")
        }
        parser.fails("-b") {
            failure(1, "No value provided for option -b")
        }
        parser.fails("-c") {
            failure(1, "No value provided for option -b")
        }
    }
}