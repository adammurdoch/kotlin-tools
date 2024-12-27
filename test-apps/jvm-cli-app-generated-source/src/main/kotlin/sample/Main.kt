package sample

import sample.lib.jvm.generated.GeneratedJvm
import sample.lib.kmp.generated.GeneratedKmp

fun main() {
    Generated().log()
    GeneratedJvm().log()
    GeneratedKmp().log()
}
