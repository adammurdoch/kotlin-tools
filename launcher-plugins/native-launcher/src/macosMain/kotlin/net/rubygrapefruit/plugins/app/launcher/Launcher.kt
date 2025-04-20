package net.rubygrapefruit.plugins.app.launcher

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.posix.*


fun setupLogging(appId: String) {
    val terminal = isatty(STDOUT_FILENO) == 1
    if (!terminal) {
        redirectStdoutAndErr(appId)
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
