package sample.system

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
fun reportSystemInfo() {
    println("Kotlin: ${KotlinVersion.CURRENT}")
    println("OS: ${Platform.osFamily} arch ${Platform.cpuArchitecture}")
    println()
}
