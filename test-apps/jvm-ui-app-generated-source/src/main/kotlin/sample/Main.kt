package sample

import sample.lib.jvm.generated.JvmLog
import sample.lib.kmp.generated.KmpLog

fun main() {
    Generated().log()
    JvmLog().log()
    KmpLog().log()
}
