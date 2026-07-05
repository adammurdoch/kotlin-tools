package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

interface HasGeneratedSource {
    val sourceSet: KotlinSourceSet

    val generatedSource: SetProperty<Directory>
}