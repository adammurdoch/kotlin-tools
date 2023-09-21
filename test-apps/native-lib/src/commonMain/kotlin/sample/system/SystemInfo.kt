package sample.system

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
fun reportSystemInfo() {
    println("OS: ${Platform.osFamily} arch ${Platform.cpuArchitecture}")
    println()
}
