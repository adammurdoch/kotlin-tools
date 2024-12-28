package sample

import sample.lib.generated.GeneratedNative
import sample.lib.kmp.generated.GeneratedKmp

actual fun log() {
    GeneratedMacOS().log()
    GeneratedKmp().log()
    GeneratedNative().log()
}