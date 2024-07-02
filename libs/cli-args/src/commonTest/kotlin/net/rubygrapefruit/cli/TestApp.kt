package net.rubygrapefruit.cli

open class TestApp(val action: Action) : Action() {
    val selected by actions {
        option(HelpAction(), "help", allowAnywhere = true)
        action(action)
    }
}

class HelpAction : Action()
