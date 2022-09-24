package net.rubygrapefruit.file.fixtures

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.FileSystem
import net.rubygrapefruit.file.RegularFile

class FilesFixture {
    val testDir by lazy {
        val baseDir = FileSystem.currentDirectory.dir("build/test files")
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

    fun dir(name: String, builder: DirFixture.() -> Unit = {}): Directory {
        val dir = testDir.dir(name)
        dir.createDirectories()
        builder(DirFixture(dir))
        return dir
    }

    class DirFixture(val dir: Directory) {
        fun file(name: String) {
            val file = dir.file(name)
            file.writeText("test")
        }

        fun dir(name: String, builder: DirFixture.() -> Unit = {}) {
            val subdir = dir.dir(name)
            subdir.createDirectories()
            builder(DirFixture(subdir))
        }
    }
}