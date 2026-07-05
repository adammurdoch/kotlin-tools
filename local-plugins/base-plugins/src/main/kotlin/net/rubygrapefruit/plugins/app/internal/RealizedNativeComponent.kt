package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

class RealizedNativeComponent(
    override val sourceSet: KotlinSourceSet,
    override val generatedSource: SetProperty<Directory>
): HasGeneratedSource