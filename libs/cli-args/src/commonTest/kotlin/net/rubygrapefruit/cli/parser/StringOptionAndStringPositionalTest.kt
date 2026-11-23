package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class StringOptionAndStringPositionalTest : AbstractParseTest() {
    @Test
    fun `parses option then positional in any order`() {
        val parser = params(option("x"), positional("a")) { x, a -> Pair(x, a) }

        parser.edges {
            nonPositional {
                matches("-x", "one") {
                    positional {
                        matches("two") {
                            endOfInput(Pair("one", "two"))
                        }
                        expectingPositional("a")
                    }
                }
            }
            positional {
                matches("two") {
                    nonPositional {
                        matches("-x", "one") {
                            endOfInput(Pair("one", "two"))
                        }
                    }
                    endOfInput(Pair(null, "two"))
                }
                expectingPositional("a")
            }
        }
    }

    @Test
    fun `parses positional then option in any order`() {
        val parser = params(positional("a"), option("x")) { a, x -> Pair(x, a) }

        assertEquals(Pair("one", "two"), parser.parse("-x", "one", "two").get())
        assertEquals(Pair("one", "two"), parser.parse("two", "-x", "one").get())
        assertEquals(Pair(null, "two"), parser.parse("two").get())
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
            expectedPositional(0, "a")
        }
        parser.fails("-x", "value") {
            expectedPositional(2, "a")
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
            expectedPositional(1, "a")
        }
    }
}