import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import net.rubygrapefruit.file.fileSystem
import net.rubygrapefruit.store.Store

private val storeDirectory = fileSystem.userHomeDirectory.dir(".todo")

class TodoApp : CliktCommand() {
    init {
        subcommands(ListCommand(), AddCommand())
    }

    override fun run() {
    }
}

class ListCommand : CliktCommand(name = "list", help = "List TODO items") {
    override fun run() {
        runBlocking {
            Store.open(storeDirectory).use { store ->
                val items = store.value<TodoItems>("items").get()
                if (items != null) {
                    println("TODO")
                    for (item in items.items.filter { !it.completed }) {
                        println("- ${item.description}")
                    }
                    for (item in items.items.filter { it.completed }) {
                        println("- [x] ${item.description}")
                    }
                } else {
                    println("No items")
                }
            }
        }
    }
}

class AddCommand : CliktCommand(name = "add") {
    private val entry by argument().multiple()

    override fun run() {
        runBlocking {
            Store.open(storeDirectory).use { store ->
                val value = store.value<TodoItems>("items")
                val items = value.get() ?: TodoItems(emptyList())
                value.set(items.add(entry.joinToString(" ")))
            }
        }
    }
}

@Serializable
data class TodoItem(
    val description: String,
    val completed: Boolean
)

@Serializable
data class TodoItems(
    val items: List<TodoItem>
) {
    fun add(description: String): TodoItems {
        return TodoItems(items + TodoItem(description, false))
    }
}

fun main(args: Array<String>) = TodoApp().main(args)
