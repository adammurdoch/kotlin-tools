package net.rubygrapefruit.plugins.app

import org.gradle.api.tasks.Nested

interface JvmLibrary: JvmComponent {
    @get:Nested
    val module: JvmModule
}