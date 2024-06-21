package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ConfigurationBuilder
import net.rubygrapefruit.file.*

open class CliAction : Action() {
    /**
     * Allows configuration values of type [ElementPath] to be added to this action.
     *
     * The provided value must be a path to a file system element. Fails if the path does not exist.
     */
    fun path(mustExist: Boolean = true): ConfigurationBuilder<ElementPath> {
        return type { path ->
            val file = fileSystem.currentDirectory.file(path)
            if (mustExist && file.metadata().missing) {
                ConversionResult.Failure("does not exist")
            } else {
                ConversionResult.Success(file.path)
            }
        }
    }

    /**
     * Allows configuration values of type [Directory] to be added to this action.
     *
     * The provided value must be a path to a directory. Fails if the path does not exist, or is not a directory.
     */
    fun dir(mustExist: Boolean = true): ConfigurationBuilder<Directory> {
        return type { path ->
            val dir = fileSystem.currentDirectory.dir(path)
            val metadata = dir.metadata()
            if (metadata.directory || (!mustExist && metadata.missing)) {
                ConversionResult.Success(dir)
            } else {
                ConversionResult.Failure("is not a directory")
            }
        }
    }

    /**
     * Allows configuration values of type [RegularFile] to be added to this action.
     *
     * The provided value must be a path to a regular file. Fails if the path does not exist, or is not a directory.
     */
    fun file(mustExist: Boolean = true): ConfigurationBuilder<RegularFile> {
        return type { path ->
            val file = fileSystem.currentDirectory.file(path)
            val metadata = file.metadata()
            if (metadata.regularFile || (!mustExist && metadata.missing)) {
                ConversionResult.Success(file)
            } else {
                ConversionResult.Failure("is not a file")
            }
        }
    }
}