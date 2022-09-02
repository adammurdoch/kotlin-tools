import kotlinx.cinterop.*
import platform.Foundation.NSBundle
import platform.posix.*

fun main() {
    memScoped {
        val appId = NSBundle.mainBundle.bundleIdentifier ?: "app"
        println("Starting application '$appId'")

        redirectStdoutAndErr(appId)

        println("Application id: $appId")
        println("Current dir: ${getCurrentDir()}")

        val bundlePath = NSBundle.mainBundle.bundlePath
        println("Bundle path: $bundlePath")

        val configFilePath = "$bundlePath/Contents/Resources/launcher.conf"
        val lines = readConfigFile(configFilePath)
        val appDisplayName = lines[0]
        val icon = lines[1]

        val javaLauncher = lines[2]
        val launcher = "$bundlePath/Contents/$javaLauncher"

        val mainClass = lines[3]

        println("Launcher: $launcher")
        println("App display name: $appDisplayName")
        println("Main class: $mainClass")

        val args = listOf(launcher, "-Xdock:name=$appDisplayName", "-Xdock:icon=$bundlePath/Contents/Resources/$icon", "-Dapple.laf.useScreenMenuBar=true", "--module", mainClass)
        execv(args.first(), (args.map { it.cstr.ptr } + listOf(null)).toCValues())
        failed("Could not launch app")
    }
}

private fun redirectStdoutAndErr(appId: String) {
    val homeDir = getHomeDir()
    val logsDir = "$homeDir/Library/Logs/$appId"
    if (mkdir(logsDir, S_IRWXU) == -1) {
        if (errno != EEXIST) {
            failed("Could not create log directory")
        }
    }
    val logFilePath = "$logsDir/$appId.log"
    val logFile = open(logFilePath, O_WRONLY.or(O_CREAT).or(O_TRUNC), S_IRUSR.or(S_IWUSR))
    if (logFile == -1) {
        failed("Could not redirect output")
    }
    println("Redirecting output to log file '$logFilePath'")
    if (dup2(logFile, STDOUT_FILENO) == -1) {
        failed("Could not redirect output")
    }
    if (dup2(logFile, STDERR_FILENO) == -1) {
        failed("Could not redirect output")
    }
}

private fun readConfigFile(configFilePath: String): List<String> {
    return memScoped {
        val configFile = fopen(configFilePath, "r")
        if (configFile == null) {
            failed("Could not read launcher config file")
        }
        try {
            val size = 1024 * 4
            val buffer = allocArray<ByteVar>(size)
            val nread = fread(buffer, 1, size.convert(), configFile)
            buffer[nread.convert()] = 0
            buffer.toKString().lines().map { it.trim() }
        } finally {
            fclose(configFile)
        }
    }
}

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

private fun getHomeDir(): String {
    val uid = getuid()
    val pwd = getpwuid(uid)
    if (pwd == null) {
        failed("Could not get user home directory.")
    }
    val homeDir = pwd.pointed.pw_dir!!.toKString()
    return homeDir
}

fun failed(message: String): Nothing {
    val code = errno
    throw RuntimeException("$message, errno=$code (${strerror(code)?.toKString()})")
}
