package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

open class DefaultSourceSet(
    override val sourceSetName: String,
    override val generatedSource: SetProperty<Directory>
) : HasDependencies, HasGeneratedSource {
    override val dependencies = DefaultDependencies()
}

class DefaultJvmSourceSet(
    sourceSetName: String,
    generatedSource: SetProperty<Directory>,
    override val generatedResources: SetProperty<Directory>
): DefaultSourceSet(sourceSetName, generatedSource), HasGeneratedResources
