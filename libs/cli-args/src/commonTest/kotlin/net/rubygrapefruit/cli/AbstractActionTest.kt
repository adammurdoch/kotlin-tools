package net.rubygrapefruit.cli

import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

abstract class AbstractActionTest {
    fun <T : Action> parse(action: T, args: List<String>, verification: (T) -> Unit) {
        action.parse(args)
        verification(action)
    }

    fun <T : Action> parse(factory: () -> T, args: List<String>, verification: (T) -> Unit) {
        val action1 = factory()
        action1.parse(args)
        verification(action1)

        val action2 = factory()
        val result2 = action2.maybeParse(args)
        assertIs<Action.Result.Success>(result2)
    }

    fun parseFails(factory: () -> Action, args: List<String>, message: String) {
        parseFails(factory, args) { e -> assertEquals(message, e.message) }
    }

    /**
     * Expects parsing fails with a problem with a positional parameter.
     */
    fun parseFails(factory: () -> Action, args: List<String>, message: String, vararg usage: String, verification: (PositionalParseException) -> Unit = {}) {
        parseFails(factory, args) { e ->
            assertIs<PositionalParseException>(e)
            assertEquals(message, e.message)
            assertEquals(usage.size, e.positional.size)
            for (index in usage.indices) {
                val expected = usage[index]
                val actual = e.positional[index]
                if (expected == "<param>") {
                    assertIs<ParameterUsage>(actual)
                } else if (expected == "<action>") {
                    assertIs<ActionParameterUsage>(actual)
                } else {
                    assertIs<LiteralUsage>(actual)
                    assertEquals(expected, actual.name)
                }
            }
            verification(e)
        }
    }

    fun parseFails(factory: () -> Action, args: List<String>, verification: (ArgParseException) -> Unit) {
        val action1 = factory()
        try {
            action1.parse(args)
            fail()
        } catch (e: ArgParseException) {
            verification(e)
        }

        val action2 = factory()
        val result2 = action2.maybeParse(args)
        assertIs<Action.Result.Failure>(result2)
        verification(result2.failure)

        parseHelp(factory, args)
    }

    private fun parseHelp(factory: () -> Action, args: List<String>) {
        for (index in args.indices) {
            val copy = args.toMutableList()
            copy.add(index, "--help")
            val app = TestApp(factory())
            val result = app.maybeParse(copy)
            assertIs<Action.Result.Success>(result)
            assertIs<HelpAction>(app.selected)
        }

        val app = TestApp(factory())
        val result = app.maybeParse(args + listOf("--help"))
        assertIs<Action.Result.Success>(result)
        assertIs<HelpAction>(app.selected)
    }
}