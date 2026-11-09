package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class ChoiceOfChoiceTest : AbstractParseTest() {
    @Test
    fun `parses choice of choice of command and option`() {
        val parser = oneOf(
            oneOf(
                oneOf(
                    params(command("one")) { 1 },
                    params(command("two")) { 2 }
                ),
                params(option("3").boolean().required()) { 3 },
            ),
            params(option("4").boolean().required()) { 4 }
        )

        assertEquals(1, parser.parse("one").get())
        assertEquals(2, parser.parse("two").get())
        assertEquals(3, parser.parse("-3").get())
        assertEquals(4, parser.parse("-4").get())
    }

    @Test
    fun `reports usage of choice of choice of command and option`() {
        val parser = oneOf(
            oneOf(
                oneOf(
                    params(command("one")) { 1 },
                    params(command("two")) { 2 }
                ),
                params(option("3").boolean().required()) { 3 },
            ),
            params(option("4").boolean().required()) { 4 }
        )

        assertEquals(
            """one
two
-3
-4""", parser.usage
        )
    }
}