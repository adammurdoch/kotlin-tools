package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals

class MultipleRequiredBooleanOptionsTest : AbstractParseTest() {
    @Test
    fun `parses three options in any order`() {
        val parser = params(
            option("a").boolean().required(),
            option("b").boolean().required(),
            option("long").boolean().required()
        ) { a, b, c -> listOf(a, b, c) }

        assertEquals(listOf(Unit, Unit, Unit), parser.parse("-a", "-b", "--long").get())
        assertEquals(listOf(Unit, Unit, Unit), parser.parse("--long", "-a", "-b").get())
    }

    @Test
    fun `reports usage for three options`() {
        val parser = params(option("a").boolean().required(), option("b").boolean().required(), option("long").boolean().required()) { a, b, c -> listOf(a, b, c) }

        assertEquals("-a -b --long", parser.usage)
    }

    @Test
    fun `options must be provided`() {
        val parser = params(
            option("a").boolean().required(),
            option("b").boolean().required(),
            option("long").boolean().required()
        ) { a, b, c -> listOf(a, b, c) }

        parser.fails {
            failure(0, "Option -a not provided")
            failure(0, "Option -b not provided")
            failure(0, "Option --long not provided")
        }
        parser.fails("-a") {
            failure(1, "Option -b not provided")
            failure(1, "Option --long not provided")
        }
        parser.fails("-b", "-a") {
            failure(2, "Option --long not provided")
        }
    }

}