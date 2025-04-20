import kotlinx.cinterop.*
import net.rubygrapefruit.plugins.app.launcher.failed
import net.rubygrapefruit.plugins.app.launcher.setupLogging
import platform.Foundation.NSBundle
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
fun main(args: Array<String>) {
    memScoped {
        setupLogging()

        println("Current dir: ${getCurrentDir()}")

        val bundlePath = NSBundle.mainBundle.bundlePath
        println("Bundle path: $bundlePath")

        val lines = readConfigFile("$bundlePath/Contents/Resources/launcher.conf")
        val appDisplayName = lines[0]
        val icon = lines[1]

        val javaLauncher = lines[2]
        val launcher = "$bundlePath/Contents/$javaLauncher"

        val mainClass = lines[3]

        println("Launcher: $launcher")
        println("App display name: $appDisplayName")
        println("Main class: $mainClass")

        val args = listOf(
            launcher,
            "-Xdock:name=$appDisplayName",
            "-Xdock:icon=$bundlePath/Contents/Resources/$icon",
            "-Dapple.laf.useScreenMenuBar=true",
            "--module",
            mainClass
        ) + args
        execv(args.first(), (args.map { it.cstr.ptr } + listOf(null)).toCValues())
        failed("Could not launch app using $launcher")
    }
}


@OptIn(ExperimentalForeignApi::class)
private fun readConfigFile(configFilePath: String): List<String> {
    return memScoped {
        val configFile = fopen(configFilePath, "r")
        if (configFile == null) {
            failed("Could not read launcher config file at '$configFilePath'")
        }
        try {
            val size = 1024 * 4
            val buffer = allocArray<ByteVar>(size)
            val nread = fread(buffer, 1.convert(), size.convert(), configFile)
            buffer[nread.convert()] = 0
            buffer.toKString().lines().map { it.trim() }
        } finally {
            fclose(configFile)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun getCurrentDir(): String {
    return memScoped {
        val length = MAXPATHLEN
        val buffer = allocArray<ByteVar>(length)
        val path = getcwd(buffer, length.convert())
        if (path == null) {
            failed("Could not get current directory.")
        }
        path.toKString()
    }
}
