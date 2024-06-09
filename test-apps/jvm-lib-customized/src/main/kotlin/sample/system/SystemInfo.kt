package sample.system

fun reportSystemInfo() {
    println("Kotlin: ${KotlinVersion.CURRENT}")
    println("JVM: ${System.getProperty("java.version")} (${System.getProperty("java.vendor")})")
    println("OS: ${System.getProperty("os.name")} (${System.getProperty("os.version")} ${System.getProperty("os.arch")})")
    println()
}
