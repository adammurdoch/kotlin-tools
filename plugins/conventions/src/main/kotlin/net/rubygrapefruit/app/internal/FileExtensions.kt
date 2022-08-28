package net.rubygrapefruit.app.internal

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes

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
