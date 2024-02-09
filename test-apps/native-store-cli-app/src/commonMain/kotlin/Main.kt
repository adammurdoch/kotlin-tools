@file:OptIn(ExperimentalStdlibApi::class)

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import net.rubygrapefruit.file.fileSystem
import net.rubygrapefruit.store.ContentVisitor
import net.rubygrapefruit.store.Store

class StoreApp : CliktCommand() {
    init {
        subcommands(ContentCommand(), BenchmarkOneValueCommand(), BenchmarkManyValuesCommand(), BenchmarkOneKeyValueCommand())
    }

    override fun run() {
    }
}

abstract class AbstractStoreCommand(name: String, help: String) : CliktCommand(name = name, help = help) {
    private val store by argument("store", help = "The store to use")

    override fun run() {
        Store.open(fileSystem.currentDirectory.dir(store)).use {
            run(it)
        }
    }

    abstract fun run(store: Store)
}

class ContentCommand : AbstractStoreCommand(name = "content", help = "Dump content") {
    override fun run(store: Store) {
        store.accept(object : ContentVisitor {
            override fun value(name: String, details: ContentVisitor.ValueInfo) {
                println("- '$name' ${details.formatted}")
            }
        })
    }
}

class BenchmarkOneValueCommand : AbstractStoreCommand(name = "one-value", help = "Run benchmark for a single value") {
    private val iterations by option("--iterations", help = "The number of iterations").int().default(10000)

    override fun run(store: Store) {
        println("Benchmarking with $iterations iterations")
        val source = ValueSource(iterations)
        val value = store.value<String>("some value")
        for (i in 0 until iterations) {
            value.set(source.values[i])
        }
        for (i in 0 until iterations) {
            val read = value.get()
            require(read == source.values.last())
        }
    }
}

class BenchmarkManyValuesCommand : AbstractStoreCommand(name = "many-values", help = "Run benchmark for many values") {
    private val values by option("--values", help = "The number of values").int().default(1000)
    private val iterations by option("--iterations", help = "The number of iterations").int().default(10)

    override fun run(store: Store) {
        println("Benchmarking with $values values and $iterations iterations")
        val source = ValueSource(iterations)
        for (v in 1..values) {
            val value = store.value<String>("some value $v")
            for (i in 0 until iterations) {
                value.set(source.values[i])
            }
        }
        for (v in 1..values) {
            val value = store.value<String>("some value $v")
            for (i in 0 until iterations) {
                val read = value.get()
                require(read == source.values.last())
            }
        }
    }
}

class BenchmarkOneKeyValueCommand : AbstractStoreCommand(name = "one-key-value", help = "Run benchmark for a single key-value store") {
    private val iterations by option("--iterations", help = "The number of iterations").int().default(10000)

    override fun run(store: Store) {
        println("Benchmarking with $iterations iterations")
        val source = ValueSource(iterations)
        val value = store.keyValue<Int, String>("some value")
        for (i in 0 until iterations) {
            value.set(i, source.values[i])
        }
        for (i in 0 until iterations) {
            val read = value.get(i)
            require(read == source.values[i])
        }
    }
}

private class ValueSource(count: Int) {
    val values: List<String> = (1..count).map { "value $it" }
}

fun main(args: Array<String>) = StoreApp().main(args)
