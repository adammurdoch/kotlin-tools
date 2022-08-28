package net.rubygrapefruit.download

import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class DownloadRepository(val silent: Boolean = false) {
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
    fun install(uri: URI, name: String): Path {
        Files.createDirectories(downloadsDir)
        val dir = downloadsDir.resolve(name)
        if (Files.exists(dir)) {
            return dir
        } else {
            val tmpFile = downloadsDir.resolve("${name}.zip-tmp")
            val tmpDir = downloadsDir.resolve("${name}.expanded-tmp")
            if (!silent) {
                println("Downloading $uri")
            }
            uri.toURL().openConnection().getInputStream().use {
                Files.copy(it, tmpFile, StandardCopyOption.REPLACE_EXISTING)
            }
            if (!exec("unzip", "-q", tmpFile.toString(), "-d", tmpDir.toString())) {
                throw RuntimeException("Could not unzip $tmpFile")
            }
            Files.move(tmpDir, dir, StandardCopyOption.ATOMIC_MOVE)
            return dir
        }
    }

    private fun exec(vararg commandLine: String): Boolean {
        val builder = ProcessBuilder(commandLine.toList())
        builder.inheritIO()
        val process = builder.start()
        val exitValue = process.waitFor()
        return exitValue == 0
    }
}