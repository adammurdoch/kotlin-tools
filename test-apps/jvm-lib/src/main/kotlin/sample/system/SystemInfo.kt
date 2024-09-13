package sample.system

data class SystemInfo(val kotlinVersion: String, val jvm: String, val os: String)

fun reportSystemInfo() {
    val systemInfo = getSystemInfo()
    println("Kotlin: ${systemInfo.kotlinVersion}")
    println("JVM: ${systemInfo.jvm}")
    println("OS: ${systemInfo.os}")
    println()
}

fun getSystemInfo(): SystemInfo {
    return SystemInfo(
        KotlinVersion.CURRENT.toString(),
        "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})",
        "${System.getProperty("os.name")} (${System.getProperty("os.version")} ${System.getProperty("os.arch")})"
    )
}
