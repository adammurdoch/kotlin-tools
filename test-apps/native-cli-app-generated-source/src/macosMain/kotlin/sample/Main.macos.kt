package sample

import sample.lib.generated.GeneratedNative
import sample.lib.kmp.generated.KmpLog

actual fun log() {
    GeneratedMacOS().log()
    KmpLog().log()
    GeneratedNative().log()
}