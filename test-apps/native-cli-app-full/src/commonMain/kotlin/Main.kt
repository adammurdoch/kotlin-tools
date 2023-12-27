import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple

class TodoApp : CliktCommand() {
    init {
        subcommands(ListCommand(), AddCommand())
    }

    override fun run() {
    }
}

class ListCommand : CliktCommand(name = "list") {
    override fun run() {
        println("list entries")
    }
}

class AddCommand : CliktCommand(name = "add") {
    private val entry by argument().multiple()

    override fun run() {
        println("add entry: $entry")
    }
}

fun main(args: Array<String>) = TodoApp().main(args)
