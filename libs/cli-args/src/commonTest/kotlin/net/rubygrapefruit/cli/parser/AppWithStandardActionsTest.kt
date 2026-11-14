package net.rubygrapefruit.cli.parser

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

class AppWithStandardActionsTest : AbstractParseTest() {
    @Test
    fun `parses choice of help or command`() {
        val start = NoParams()
        val stop = NoParams()
        val parser = app(
            "some_app",
            oneOf(
                params(command("start")) { start },
                params(command("stop")) { stop }
            ),
            helpAction = true,
            completionAction = false
        )

        assertSame(start, parser.parse("start").get())
        assertSame(stop, parser.parse("stop").get())
        assertIs<HelpAction>(parser.parse("--help").get())
        assertIs<HelpAction>(parser.parse("-h").get())
    }

    @Test
    fun `parses choice of completion or command`() {
        val start = NoParams()
        val stop = NoParams()
        val parser = app(
            "some_app",
            oneOf(
                params(command("start")) { start },
                params(command("stop")) { stop }
            ),
            helpAction = false,
            completionAction = true
        )

        assertSame(start, parser.parse("start").get())
        assertSame(stop, parser.parse("stop").get())
        assertIs<CompletionAction>(parser.parse("--completion").get())
    }

    @Test
    fun `parses choice of completion or help or command`() {
        val start = NoParams()
        val stop = NoParams()
        val parser = app(
            "some_app",
            oneOf(
                params(command("start")) { start },
                params(command("stop")) { stop }
            ),
            helpAction = true,
            completionAction = true
        )

        assertSame(start, parser.parse("start").get())
        assertSame(stop, parser.parse("stop").get())
        assertIs<HelpAction>(parser.parse("--help").get())
        assertIs<CompletionAction>(parser.parse("--completion").get())
    }
}