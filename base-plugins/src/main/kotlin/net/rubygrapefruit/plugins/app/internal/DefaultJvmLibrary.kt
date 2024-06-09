package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.JvmLibrary
import org.gradle.api.Project
import javax.inject.Inject

abstract class DefaultJvmLibrary @Inject constructor(
    project: Project,
    mainSourceSetName: String
) : DefaultJvmComponent(project, mainSourceSetName), JvmLibrary