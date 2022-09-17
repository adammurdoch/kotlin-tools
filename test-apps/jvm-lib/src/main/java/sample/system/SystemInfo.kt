package sample.system

fun reportSystemInfo() {
    println("Java version: ${System.getProperty("java.version")}")
    println("Java vendor: ${System.getProperty("java.vendor")}")
    println("OS name: ${System.getProperty("os.name")}")
    println("OS architecture: ${System.getProperty("os.arch")}")
    println("OS version: ${System.getProperty("os.version")}")
    println()
}
