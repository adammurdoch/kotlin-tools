package net.rubygrapefruit.file.fixtures

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.file.SymLink
import net.rubygrapefruit.file.fileSystem

class FilesFixture {
    val testDir by lazy {
        val baseDir = fileSystem.currentDirectory.dir("build/test files")
        baseDir.createDirectories()
        baseDir.createTemporaryDirectory()
    }

    operator fun invoke(builder: DirFixture.() -> Unit) {
        files(builder)
    }

    fun files(builder: DirFixture.() -> Unit) {
        builder(DirFixture(testDir))
    }

    fun file(name: String): RegularFile {
        val file = testDir.file(name)
        file.writeText("test")
        return file
    }

    fun symlink(name: String, target: String): SymLink {
        val symLink = testDir.symLink(name)
        symLink.writeSymLink(target)
        return symLink
    }

    fun dir(name: String, builder: DirFixture.() -> Unit = {}): Directory {
        val dir = testDir.dir(name)
        dir.createDirectories()
        builder(DirFixture(dir))
        return dir
    }

    class DirFixture(val dir: Directory) {
        fun file(name: String, text: String = "test") {
            val file = dir.file(name)
            file.writeText(text)
        }

        fun dir(name: String, builder: DirFixture.() -> Unit = {}) {
            val subdir = dir.dir(name)
            subdir.createDirectories()
            builder(DirFixture(subdir))
        }

        fun symLink(name: String, path: String) {
            dir.symLink(name).writeSymLink(path)
        }
    }
}