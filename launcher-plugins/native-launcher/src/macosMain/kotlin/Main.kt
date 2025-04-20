import kotlinx.cinterop.*
import platform.Foundation.NSBundle
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
fun main() {
    memScoped {
        val appId = NSBundle.mainBundle.bundleIdentifier ?: "app"
        println("Starting application '$appId'")

        val terminal = isatty(STDOUT_FILENO) == 1
        if (!terminal) {
            redirectStdoutAndErr(appId)
        }

        println("Application id: $appId")
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

        val args = listOf(launcher, "-Xdock:name=$appDisplayName", "-Xdock:icon=$bundlePath/Contents/Resources/$icon", "-Dapple.laf.useScreenMenuBar=true", "--module", mainClass)
        execv(args.first(), (args.map { it.cstr.ptr } + listOf(null)).toCValues())
        failed("Could not launch app using $launcher")
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun redirectStdoutAndErr(appId: String) {
    val homeDir = getHomeDir()
    val logsDir = "$homeDir/Library/Logs/$appId"
    if (mkdir(logsDir, S_IRWXU.convert()) == -1) {
        if (errno != EEXIST) {
            failed("Could not create log directory at $logsDir")
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

@OptIn(ExperimentalForeignApi::class)
private fun getHomeDir(): String {
    val uid = getuid()
    val pwd = getpwuid(uid)
    if (pwd == null) {
        failed("Could not get user home directory.")
    }
    return pwd.pointed.pw_dir!!.toKString()
}

@OptIn(ExperimentalForeignApi::class)
fun failed(message: String): Nothing {
    val code = errno
    throw RuntimeException("$message, errno=$code (${strerror(code)?.toKString()})")
}
