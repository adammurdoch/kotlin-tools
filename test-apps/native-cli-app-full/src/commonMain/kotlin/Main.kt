import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import kotlinx.coroutines.runBlocking

class TodoApp : CliktCommand() {
    init {
        subcommands(ListCommand(), AddCommand())
    }

    override fun run() {
    }
}

class ListCommand : CliktCommand(name = "list") {
    override fun run() {
        runBlocking {
            println("list entries")
        }
    }
}

class AddCommand : CliktCommand(name = "add") {
    private val entry by argument().multiple()

    override fun run() {
        runBlocking {
            println("add entry: $entry")
        }
    }
}

fun main(args: Array<String>) = TodoApp().main(args)
