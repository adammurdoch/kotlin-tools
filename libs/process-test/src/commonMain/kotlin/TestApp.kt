import net.rubygrapefruit.cli.app.CliAction
import net.rubygrapefruit.cli.app.CliApp
import kotlin.system.exitProcess

class TestApp : CliApp("test") {
    private val action by actions {
        action(EchoAction(), "echo")
        action(CountAction(), "count")
        action(FailAction(), "fail")
        action(DefaultAction())
    }

    override fun run() {
        action.run()
    }
}

class DefaultAction : CliAction() {
    override fun run() {
        println("this is the test app")
    }
}

class FailAction : CliAction() {
    val exitCode by int().parameter("exitCode").whenAbsent(1)

    override fun run() {
        exitProcess(exitCode)
    }
}

class CountAction : CliAction() {
    val number by int().parameter("number")

    override fun run() {
        for (i in 1..number) {
            println(i)
        }
    }
}

class EchoAction : CliAction() {
    val message by parameter("message")

    override fun run() {
        println(message)
    }
}

fun main(args: Array<String>) = TestApp().run(args)
