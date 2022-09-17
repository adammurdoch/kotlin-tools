package sample.system

fun reportSystemInfo() {
    println("Operating system: ${Platform.osFamily}")
    println("Architecture: ${Platform.cpuArchitecture}")
    println()
}
