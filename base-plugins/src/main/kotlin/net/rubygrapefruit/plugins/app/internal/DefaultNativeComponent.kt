package net.rubygrapefruit.plugins.app.internal

import net.rubygrapefruit.plugins.app.Dependencies
import net.rubygrapefruit.plugins.app.NativeComponent
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import javax.inject.Inject

abstract class DefaultNativeComponent @Inject constructor(
    private val project: Project,
    private val mainSourceSetName: String
) : NativeComponent<Dependencies> {
    private val mainSourceSet
        get() = project.extensions.getByType(KotlinProjectExtension::class.java).sourceSets.getByName(mainSourceSetName)

    override fun dependencies(config: Dependencies.() -> Unit) {
        TODO("Not yet implemented")
    }

    fun attach() {
//        mainSourceSet.kotlin.srcDirs(generatedSource)
    }
}