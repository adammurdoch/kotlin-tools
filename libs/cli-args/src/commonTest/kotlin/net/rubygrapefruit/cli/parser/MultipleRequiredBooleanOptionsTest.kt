package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleRequiredBooleanOptionsTest : AbstractParseTest() {
    @Test
    fun `parses three options in any order`() {
        val parser = params(
            option("a").boolean().required(),
            option("b").boolean().required(),
            option("c").boolean().required()
        ) { a, b, c -> listOf(a, b, c) }

        assertEquals(listOf(Unit, Unit, Unit), parser.parse("-a", "-b", "-c").get())
        assertEquals(listOf(Unit, Unit, Unit), parser.parse("-c", "-a", "-b").get())
    }

    @Test
    fun `reports usage for three options`() {
        val parser = params(option("a").boolean().required(), option("b").boolean().required(), option("long").boolean().required()) { a, b, c -> listOf(a, b, c) }

        assertEquals("-a -b --long", parser.usage)
    }

}