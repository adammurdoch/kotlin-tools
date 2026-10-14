package net.rubygrapefruit.plugins.app.internal.plugins

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun Project.addGeneratedProductionSourceSet(name: String): SourceSetDetails {
    val sourceSets = extensions.getByType(SourceSetContainer::class.java)

    val sourceSet = sourceSets.create(name)
    sourceSet.java.setSrcDirs(emptyList<String>())
    sourceSet.resources.setSrcDirs(emptyList<String>())
    val kotlin = extensions.getByType(KotlinJvmProjectExtension::class.java)
    val kotlinSourceSet = kotlin.sourceSets.getByName(name)
    kotlinSourceSet.kotlin.setSrcDirs(emptyList<String>())

    kotlinSourceSet.dependencies {
        implementation(sourceSets.getByName("main").output)
    }
    tasks.named("jar", Jar::class.java) {
        it.from(sourceSet.output)
    }

    return SourceSetDetails(kotlinSourceSet)
}

class SourceSetDetails(val kotlinSourceSet: KotlinSourceSet)