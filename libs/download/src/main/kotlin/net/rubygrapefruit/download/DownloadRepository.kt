@file:OptIn(ExperimentalPathApi::class)

package net.rubygrapefruit.download

import net.rubygrapefruit.machine.info.Machine
import java.io.File
import java.net.URI
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import java.util.zip.ZipInputStream
import kotlin.concurrent.withLock
import kotlin.io.path.*

class DownloadRepository(private val silent: Boolean = false) {
    companion object {
        val locks = ConcurrentHashMap<String, ReentrantLock>()
    }

    private val downloadsDir = File(System.getProperty("user.home"), "bin/downloads").toPath()

    /**
     * Returns the directory containing the given installation, if present.
     */
    fun dir(name: String): Path? {
        val dir = downloadsDir.resolve(name)
        return if (Files.isDirectory(dir)) {
            dir
        } else {
            null
        }
    }

    /**
     * Downloads and installs the given Zip
     */
    fun install(uri: URI, name: String, configure: Actions.() -> Unit = {}): Path {
        val actions = DefaultActions()
        configure(actions)

        val format = when {
            uri.path.endsWith(Zip.extension, true) -> Zip
            uri.path.endsWith(TarGz.extension, true) -> TarGz
            else -> throw RuntimeException("Don't know how to install $uri")
        }

        downloadsDir.createDirectories()
        val installDir = downloadsDir.resolve(name)

        val workDir = downloadsDir.resolve("work")
        workDir.createDirectories()
        val markerFile = workDir.resolve("${name}.done")

        if (installDir.isDirectory() && markerFile.isRegularFile()) {
            return installDir
        }

        return maybeInstall(uri, name, format, markerFile, workDir, installDir, actions)
    }

    private fun maybeInstall(
        uri: URI,
        name: String,
        format: Format,
        markerFile: Path,
        workDir: Path,
        installDir: Path,
        actions: DefaultActions
    ): Path {
        // Block until no other threads are installing
        return locks.computeIfAbsent(name) { ReentrantLock() }.withLock {
            maybeInstallHoldingThreadLock(uri, name, format, markerFile, workDir, installDir, actions)
        }
    }

    private fun maybeInstallHoldingThreadLock(
        uri: URI,
        name: String,
        format: Format,
        markerFile: Path,
        workDir: Path,
        installDir: Path,
        actions: DefaultActions
    ): Path {
        val lockFile = workDir.resolve("${name}.lock")
        val tmpFile = workDir.resolve("${name}.download.${format.extension}")
        val tmpDir = workDir.resolve("${name}.expanded-archive")

        FileChannel.open(lockFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE).use { channel ->
            val lock = channel.lock()
            try {
                // May have been installed while waiting to acquire the lock
                if (installDir.isDirectory() && markerFile.isRegularFile()) {
                    return installDir
                }

                // Clean up any partially installed left-overs
                if (installDir.isDirectory()) {
                    installDir.deleteRecursively()
                }
                markerFile.deleteIfExists()
                if (tmpDir.isDirectory()) {
                    tmpDir.deleteRecursively()
                }

                if (!silent) {
                    println("Downloading $uri")
                }
                try {
                    tmpFile.parent.createDirectories()
                    uri.toURL().openConnection().getInputStream().use { instr ->
                        Files.copy(instr, tmpFile, StandardCopyOption.REPLACE_EXISTING)
                    }
                    require(tmpFile.isRegularFile())
                    actions.download(tmpFile)

                    tmpDir.createDirectories()
                    format.unpack(tmpFile, tmpDir)
                    actions.install(tmpDir)

                    Files.move(tmpDir, installDir, StandardCopyOption.ATOMIC_MOVE)
                    markerFile.createFile()
                } finally {
                    tmpFile.deleteIfExists()
                }
            } finally {
                lock.release()
            }
        }

        return installDir
    }

    interface Actions {
        /**
         * Called when the distribution has been downloaded and prior to it being expanded.
         */
        fun onDownload(action: (Path) -> Unit)

        /**
         * Called when the distribution has been expanded and prior to it being made visible.
         */
        fun onInstall(action: (Path) -> Unit)
    }

    private class DefaultActions : Actions {
        var install: (Path) -> Unit = {}
        var download: (Path) -> Unit = {}

        override fun onDownload(action: (Path) -> Unit) {
            download = action
        }

        override fun onInstall(action: (Path) -> Unit) {
            install = action
        }
    }

    private sealed interface Format {
        val extension: String

        fun unpack(file: Path, dir: Path)
    }

    private object Zip : Format {
        override val extension: String
            get() = "zip"

        override fun unpack(file: Path, dir: Path) {
            if (Machine.thisMachine.isWindows) {
                file.inputStream().use { stream ->
                    val zip = ZipInputStream(stream)
                    while (true) {
                        val entry = zip.nextEntry
                        if (entry == null) {
                            break
                        }
                        val target = dir.resolve(entry.name)
                        require(target.startsWith(dir))
                        if (entry.isDirectory) {
                            target.createDirectories()
                        } else {
                            target.parent.createDirectories()
                            target.outputStream().use {
                                zip.copyTo(it)
                            }
                        }
                    }
                }
            } else if (!exec("unzip", "-q", file.toString(), "-d", dir.toString())) {
                throw RuntimeException("Could not unzip $file")
            }
        }
    }

    private object TarGz : Format {
        override val extension: String
            get() = "tar.gz"

        override fun unpack(file: Path, dir: Path) {
            if (!exec("tar", "--extract", "-f", file.toString(), "--directory", dir.toString())) {
                throw RuntimeException("Could not untar $file")
            }
        }
    }
}

private fun exec(vararg commandLine: String): Boolean {
    val builder = ProcessBuilder(commandLine.toList())
    builder.inheritIO()
    val process = builder.start()
    val exitValue = process.waitFor()
    return exitValue == 0
}
