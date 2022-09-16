package net.rubygrapefruit.app

import org.gradle.api.tasks.Nested

interface JvmLibrary {
    @get:Nested
    val module: JvmModule
}
