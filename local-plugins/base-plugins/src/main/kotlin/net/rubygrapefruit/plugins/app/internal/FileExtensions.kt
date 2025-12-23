package net.rubygrapefruit.plugins.app.internal

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.createDirectories
import kotlin.io.path.name

internal fun Path.makeEmpty() {
    Files.walkFileTree(this, object : FileVisitor<Path?> {
        override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
            Files.delete(file)
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
            if (dir != this@makeEmpty) {
                Files.delete(dir)
            }
            return FileVisitResult.CONTINUE
        }
    })
}

fun copyDir(source: Path, target: Path) {
    target.createDirectories()
    Files.walkFileTree(source, object : FileVisitor<Path?> {
        val path = mutableListOf<String>()
        override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
            if (dir != source) {
                path.add(dir!!.name)
                val targetDir = target.resolve(path.joinToString("/"))
                Files.createDirectory(targetDir)
            } else {
                path.add(".")
            }
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
            val targetFile = target.resolve(path.joinToString("/") + "/${file!!.name}")
            Files.copy(file, targetFile)
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
            path.removeLast()
            return FileVisitResult.CONTINUE
        }
    })
}