package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

open class DefaultLibrarySourceSet(
    override val sourceSetName: String,
    override val generatedSource: SetProperty<Directory>
) : HasDependencies, HasGeneratedSource {
    override val dependencies = DefaultLibraryDependencies()
}

class DefaultJvmLibrarySourceSet(
    sourceSetName: String,
    generatedSource: SetProperty<Directory>,
    override val generatedResources: SetProperty<Directory>
) : DefaultLibrarySourceSet(sourceSetName, generatedSource), HasGeneratedResources
