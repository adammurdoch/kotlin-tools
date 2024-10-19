import kotlinx.io.Buffer
import kotlinx.io.buffered
import kotlinx.io.readString
import kotlinx.io.writeCodePointValue
import net.rubygrapefruit.cli.app.CliAction
import net.rubygrapefruit.cli.app.CliApp
import net.rubygrapefruit.file.fileSystem
import net.rubygrapefruit.io.stream.stdin
import net.rubygrapefruit.io.stream.stdout
import kotlin.system.exitProcess

class TestApp : CliApp("test") {
    private val action by actions {
        action(PwdAction(), "pwd")
        action(EchoAction(), "echo")
        action(CountAction(), "count")
        action(HeadAction(), "head")
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
    val count by int().parameter("count")

    override fun run() {
        for (i in 1..count) {
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

class HeadAction : CliAction() {
    val count by int().parameter("count")

    override fun run() {
        val sink = stdout.buffered()
        try {
            var lines = 0
            while (true) {
                val buffer = Buffer()
                val nread = stdin.readAtMostTo(buffer, 1024)
                if (nread < 0) {
                    return
                }
                val text = buffer.readString()
                for (ch in text) {
                    sink.writeCodePointValue(ch.code)
                    if (ch == '\n') {
                        lines++
                    }
                    if (lines >= count) {
                        return
                    }
                }
            }
        } finally {
            sink.flush()
        }
    }
}

class PwdAction : CliAction() {
    override fun run() {
        println(fileSystem.currentDirectory.absolutePath)
    }
}

fun main(args: Array<String>) = TestApp().run(args)
