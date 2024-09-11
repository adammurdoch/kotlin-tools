package net.rubygrapefruit.plugins.app

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

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
     * The outputs for this distribution
     */
    val outputs: Outputs

    interface Outputs {
        /**
         * The final distribution image. You can use this to use the distribution from other tasks, e.g. an `install` or `zip` task
         */
        val imageDirectory: Provider<Directory>

        /**
         * The launcher file in the distribution image. You can use this to use the launcher from other tasks, e.g. a `run` task.
         *
         * Has no value when this distribution cannot be run on this host machine.
         */
        val launcherFile: Provider<RegularFile>
    }
}
