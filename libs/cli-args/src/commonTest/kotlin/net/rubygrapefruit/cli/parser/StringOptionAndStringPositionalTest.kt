package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringOptionAndStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses option then positional in any order`() {
        val parser = params(option("x"), positional("a")) { x, a -> Pair(x, a) }

        assertEquals(Pair("one", "two"), parser.parse("-x", "one", "two").get())
        assertEquals(Pair("one", "two"), parser.parse("two", "-x", "one").get())
        assertEquals(Pair(null, "two"), parser.parse("two").get())
    }

    @Test
    fun `parses positional then option in any order`() {
        val parser = params(positional("a"), option("x")) { a, x -> Pair(x, a) }

        assertEquals(Pair("one", "two"), parser.parse("-x", "one", "two").get())
        assertEquals(Pair("one", "two"), parser.parse("two", "-x", "one").get())
        assertEquals(Pair(null, "two"), parser.parse("two").get())
    }

    @Test
    fun `composes option then positional`() {
        val parser = params(option("x"), positional("a")) { x, a -> Pair(x, a) }

        parser.edges {
            nonPositional {
                matches("-x", "one") {
                    skip()
//                    positional {
//                        matches("two") {
//                            endOfInput(listOf("one", "two"))
//                        }
//                    }
                }
            }
            positional {
                matches("two") {
                    skip()
//                    nonPositional {
//                        matches("-x", "one") {
//                            endOfInput(listOf("one", "two"))
//                        }
//                    }
//                    endOfInput(listOf(null, "two"))
                }
            }
        }
    }

    @Test
    fun `reports usage`() {
        val parser = params(option("x"), positional("a")) { _, _ -> fail() }

        parser.usage("[-x <value>] <a>")
        parser.option("-x <value>")
        parser.positional("<a>")
    }

    @Test
    fun `must provide positional`() {
        val parser = params(option("x"), positional("a")) { _, _ -> fail() }

        parser.fails {
            failure(0, "Parameter <a> not provided")
        }
        parser.fails("-x", "value") {
            failure(2, "Parameter <a> not provided")
        }
    }

    @Test
    fun `option must have a value`() {
        val parser = params(option("x"), positional("a")) { _, _ -> fail() }

        parser.fails("value", "-x") {
            failure(2, "No value provided for option -x")
        }
        parser.fails("-x") {
            failure(1, "No value provided for option -x")
            failure(1, "Parameter <a> not provided")
        }
    }
}