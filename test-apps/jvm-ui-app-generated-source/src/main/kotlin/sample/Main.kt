package sample

import sample.lib.jvm.generated.JvmLog
import sample.lib.kmp.generated.KmpLog

fun main(args: List<String>) {
    JvmLog().log()
    KmpLog().log()
    val message = Generated::class.java.classLoader.getResource("jvm-app-message.txt")!!.readText()
    println(message)
    Generated().log()
}
