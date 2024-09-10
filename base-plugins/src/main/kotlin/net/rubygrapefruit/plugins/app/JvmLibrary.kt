package net.rubygrapefruit.plugins.app

import org.gradle.api.tasks.Nested

interface JvmLibrary : JvmComponent<LibraryDependencies> {
    /**
     * The module for this library.
     */
    @get:Nested
    val module: JvmModule
}
