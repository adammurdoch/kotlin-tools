package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

/**
 * Represents an image of the application that can be installed somewhere. Some distributions can also be executed.
 */
interface Distribution {
    /**
     * An identifier for this distribution.
     */
    val name: String

    /**
     * The directory to create the distribution image in.
     */
    val imageDirectory: DirectoryProperty

    /**
     * The outputs for this distribution.
     */
    val outputs: Outputs

    interface Outputs {
        /**
         * The final distribution image. You can use this to use the distribution as input to tasks, for example, an `install` or `archive` task
         */
        val imageDirectory: Provider<Directory>

        /**
         * The launcher file in the distribution image. You can use this to use the launcher as input to tasks, for example, a `run` task.
         *
         * This property has no value when this distribution cannot be run on this host machine.
         */
        val launcherFile: Provider<RegularFile>
    }
}
