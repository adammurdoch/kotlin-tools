package net.rubygrapefruit.cli.app

import net.rubygrapefruit.cli.Action
import net.rubygrapefruit.cli.ConfigurationBuilder
import net.rubygrapefruit.file.ElementPath
import net.rubygrapefruit.file.fileSystem

open class CliAction : Action() {
    /**
     * Allows configuration values of type [ElementPath] to be added to this action.
     */
    fun path(): ConfigurationBuilder<ElementPath> {
        return type { fileSystem.currentDirectory.path.resolve(it) }
    }
}