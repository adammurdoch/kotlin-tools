package net.rubygrapefruit.cli.files

import net.rubygrapefruit.cli.parser.StringType
import net.rubygrapefruit.cli.parser.map
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.FileSystem
import net.rubygrapefruit.file.RegularFile

fun inputFile(): StringType<RegularFile> {
    return map("a file") { path ->
        val file = FileSystem.local.currentDirectory.file(path)
        StringType.Success(file)
    }
}

fun outputDirectory(): StringType<Directory> {
    return map("a file") { path ->
        val dir = FileSystem.local.currentDirectory.dir(path)
        StringType.Success(dir)
    }
}