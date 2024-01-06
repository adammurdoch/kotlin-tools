package sample

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.serializer
import net.rubygrapefruit.file.toDir
import net.rubygrapefruit.store.Store

class StoreApp : CliktCommand() {
    init {
        subcommands(BenchmarkCommand())
    }

    override fun run() {
    }
}

class BenchmarkCommand : CliktCommand(name = "benchmark", help = "Run benchmark") {
    private val dir by argument("store", help = "The store to benchmark").file()
    private val iterations by option("--iterations", help = "The number of iterations").int().default(1000)

    override fun run() {
        println("Benchmarking with $iterations iterations")
        Store.open(dir.toDir()).use {
            val value = it.value("thing", serializer<String>())
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
