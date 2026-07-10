package net.rubygrapefruit.plugins.app.internal

import org.gradle.api.file.Directory
import org.gradle.api.provider.SetProperty

class DefaultSourceSet(
    override val sourceSetName: String,
    override val dependencies: DefaultDependencies,
    override val generatedSource: SetProperty<Directory>
) : HasDependencies, HasGeneratedSource