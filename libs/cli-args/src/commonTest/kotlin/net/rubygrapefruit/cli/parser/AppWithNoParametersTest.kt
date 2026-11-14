package net.rubygrapefruit.cli.parser

import kotlin.test.Test

class AppWithNoParametersTest: AbstractParseTest() {
    @Test
    fun `reports app usage`() {
        val parser = app(
            "some_app",
            params { NoParams() },
            helpAction = false,
            completionAction = false
        )

        parser.usage("some_app")
        parser.noOptions()
    }
}