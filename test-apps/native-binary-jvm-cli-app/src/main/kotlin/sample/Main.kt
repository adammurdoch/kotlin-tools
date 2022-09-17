package sample

import sample.system.reportSystemInfo

fun main(args: Array<String>) {
    reportSystemInfo()
    println("Arguments: ${args.joinToString(", ")}")
}
