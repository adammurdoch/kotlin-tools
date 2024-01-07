package sample

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import net.rubygrapefruit.file.toDir
import net.rubygrapefruit.store.ContentVisitor
import net.rubygrapefruit.store.Store

class StoreApp : CliktCommand() {
    init {
        subcommands(ContentCommand(), BenchmarkOneValueCommand(), BenchmarkManyValuesCommand())
    }

    override fun run() {
    }
}

class ContentCommand : CliktCommand(name = "content", help = "Dump content") {
    private val store by argument("store", help = "The store to use").file()

    override fun run() {
        Store.open(store.toDir()).use { store ->
            store.accept(object : ContentVisitor {
                override fun value(name: String, details: ContentVisitor.ValueInfo) {
                    println("- '$name' address ${details.address}")
                }
            })
        }
    }
}

abstract class AbstractBenchmarkCommand(name: String, help: String) : CliktCommand(name = name, help = help) {
    private val store by argument("store", help = "The store to benchmark").file()

    override fun run() {
        Store.open(store.toDir()).use { store ->
            benchmark(store)
        }
    }

    abstract fun benchmark(store: Store)
}

class BenchmarkOneValueCommand : AbstractBenchmarkCommand(name = "one-value", help = "Run benchmark for a single value") {
    private val iterations by option("--iterations", help = "The number of iterations").int().default(10000)

    override fun benchmark(store: Store) {
        println("Benchmarking with $iterations iterations")
        val value = store.value<String>("some value")
        for (i in 1..iterations) {
            value.set("value $i")
        }
        for (i in 1..iterations) {
            value.get()
        }
    }
}

class BenchmarkManyValuesCommand : AbstractBenchmarkCommand(name = "many-values", help = "Run benchmark for many values") {
    private val values by option("--values", help = "The number of values").int().default(1000)
    private val iterations by option("--iterations", help = "The number of iterations").int().default(10)

    override fun benchmark(store: Store) {
        println("Benchmarking with $values values and $iterations iterations")
        for (v in 1..values) {
            val value = store.value<String>("some value $v")
            for (i in 1..iterations) {
                value.set("value $i")
            }
            for (i in 1..iterations) {
                value.get()
            }
        }
    }
}

fun main(args: Array<String>) = StoreApp().main(args)
