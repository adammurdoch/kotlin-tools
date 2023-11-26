package net.rubygrapefruit.download

import net.rubygrapefruit.machine.info.Machine
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

class DownloadRepository(private val silent: Boolean = false) {
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
    fun install(uri: URI, name: String, onInstall: (Path) -> Unit = {}): Path {
        Files.createDirectories(downloadsDir)
        val dir = downloadsDir.resolve(name)
        if (Files.exists(dir)) {
            return dir
        } else {
            val format = when {
                uri.path.endsWith(Zip.extension, true) -> Zip
                uri.path.endsWith(TarGz.extension, true) -> TarGz
                else -> throw RuntimeException("Don't know how to install $uri")
            }

            val tmpFile = downloadsDir.resolve("${name}.download.${format.extension}")
            val tmpDir = downloadsDir.resolve("${name}.expanded-tmp")
            Files.createDirectories(tmpDir)
            if (!silent) {
                println("Downloading $uri")
            }
            try {
                uri.toURL().openConnection().getInputStream().use {
                    Files.copy(it, tmpFile, StandardCopyOption.REPLACE_EXISTING)
                }
                format.unpack(tmpFile, tmpDir)
                Files.move(tmpDir, dir, StandardCopyOption.ATOMIC_MOVE)
                onInstall(dir)
            } finally {
                Files.deleteIfExists(tmpFile)
            }
            return dir
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
            if (Machine.thisMachine is Machine.Windows) {
                file.inputStream().use { stream ->
                    val zip = ZipInputStream(stream)
                    while(true) {
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
