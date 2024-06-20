package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ConfigurationBuilder
import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.ElementPath
import net.rubygrapefruit.file.directory
import net.rubygrapefruit.file.fileSystem

open class CliAction : Action() {
    /**
     * Allows configuration values of type [ElementPath] to be added to this action.
     */
    fun path(): ConfigurationBuilder<ElementPath> {
        return type { path -> fileSystem.currentDirectory.path.resolve(path) }
    }

    /**
     * Allows configuration values of type [Directory] to be added to this action.
     *
     * The provided value must be a path to a directory. Fails if the path does not exist, or is not a directory.
     */
    fun dir(): ConfigurationBuilder<Directory> {
        return type { path ->
            val dir = fileSystem.currentDirectory.dir(path)
            if (dir.metadata().directory) {
                dir
            } else {
                null
            }
        }
    }
}