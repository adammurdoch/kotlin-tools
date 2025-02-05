package sample

import net.rubygrapefruit.cli.app.CliAction
import net.rubygrapefruit.cli.app.CliApp

class App : CliApp("cli-args-test") {
    private val flag by flag("f", "flag", help = "Enable some flag")
    private val action by action {
        action(ActionWithParameter(), "action", help = "an action")
        action(ActionWithActions(), "nested", help = "an action")
        option(ActionWithParameter(), "option", help = "an option")
        action(ActionWithParameter())
    }

    override fun run() {
        println("flag: $flag")
        action.run()
    }
}

class ActionWithParameter : CliAction() {
    private val param by parameter("param")

    override fun run() {
        println("param: $param")
    }
}

class ActionWithActions : CliAction() {
    private val param by parameter("param")
    private val action by action {
        action(ActionWithParameter(), "action", help = "an action")
        option(ActionWithParameter(), "option", help = "an option")
        action(ActionWithParameter())
    }

    override fun run() {
        println("param: $param")
        action.run()
    }
}

fun main(args: Array<String>) = App().run(args)
