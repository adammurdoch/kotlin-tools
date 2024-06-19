package sample

import net.rubygrapefruit.cli.app.CliAction
import net.rubygrapefruit.cli.app.CliApp
import net.rubygrapefruit.file.fileSystem
import net.rubygrapefruit.store.ContentVisitor
import net.rubygrapefruit.store.Store

class StoreApp : CliApp("jvm-store-cli-app") {
    private val command by actions {
        action(ContentCommand(), "content", help = "Dump content")
        action(BenchmarkOneValueCommand(), "one-value", help = "Run benchmark for a single value")
        action(BenchmarkManyValuesCommand(), "many-values", help = "Run benchmark for many values")
        action(BenchmarkOneKeyValueCommand(), "one-map", help = "Run benchmark for a single map")
    }

    override fun run() {
        command.run()
    }
}

abstract class AbstractStoreCommand : CliAction() {
    private val store by path().parameter("store", help = "The store to use")

    override fun run() {
        val dir = fileSystem.currentDirectory.dir(store.absolutePath)
        Store.open(dir, discard = discard).use {
            run(it)
        }
    }

    open val discard: Boolean
        get() = true

    abstract fun run(store: Store)
}

class ContentCommand : AbstractStoreCommand() {
    override val discard: Boolean
        get() = false

    override fun run(store: Store) {
        store.accept(object : ContentVisitor {
            override fun value(name: String, details: ContentVisitor.ValueInfo) {
                println("- '$name' ${details.formatted}")
            }
        })
    }
}

class BenchmarkOneValueCommand : AbstractStoreCommand() {
    private val iterations by int().option("iterations", help = "The number of iterations").whenAbsent(200000)

    override fun run(store: Store) {
        println("Benchmarking with $iterations iterations")
        val source = ValueSource(iterations)
        val value = store.value<String>("value")
        for (i in 0 until iterations) {
            value.set(source.values[i])
        }
        for (i in 0 until iterations) {
            val read = value.get()
            require(read == source.values.last())
        }
    }
}

class BenchmarkManyValuesCommand : AbstractStoreCommand() {
    private val values by int().option("values", help = "The number of values").whenAbsent(1000)
    private val iterations by int().option("iterations", help = "The number of iterations").whenAbsent(200)

    override fun run(store: Store) {
        println("Benchmarking with $values values and $iterations iterations")
        val source = ValueSource(iterations)
        for (v in 1..values) {
            val value = store.value<String>("value $v")
            for (i in 0 until iterations) {
                value.set(source.values[i])
            }
        }
        for (v in 1..values) {
            val value = store.value<String>("value $v")
            for (i in 0 until iterations) {
                val read = value.get()
                require(read == source.values.last())
            }
        }
    }
}

class BenchmarkOneKeyValueCommand : AbstractStoreCommand() {
    private val entries by int().option("entries", help = "The number of entries").whenAbsent(1500)
    private val iterations by int().option("iterations", help = "The number of iterations").whenAbsent(100)

    override fun run(store: Store) {
        println("Benchmarking with $entries entries and $iterations iterations")
        val source = ValueSource(iterations)
        val value = store.map<Int, String>("map")
        for (i in 0 until iterations) {
            for (e in 0 until entries) {
                value.set(e, source.values[i])
            }
        }
        for (i in 0 until iterations) {
            for (e in 0 until entries) {
                val read = value.get(e)
                require(read == source.values.last())
            }
        }
    }
}

private class ValueSource(count: Int) {
    val values: List<String> = (1..count).map { "value $it" }
}

fun main(args: Array<String>) = StoreApp().run(args)
