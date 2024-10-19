import net.rubygrapefruit.cli.app.CliApp

class TestApp : CliApp("test") {
    override fun run() {
        println("test app")
    }
}

fun main(args: Array<String>) = TestApp().run(args)
