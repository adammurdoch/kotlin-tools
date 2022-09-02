package net.rubygrapefruit.app.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import org.jetbrains.kotlin.konan.file.recursiveCopyTo
import javax.inject.Inject
import kotlin.io.path.name
import kotlin.io.path.pathString

abstract class ReleaseDistribution : DefaultTask() {
    @get:OutputDirectory
    abstract val imageDirectory: DirectoryProperty

    @get:InputDirectory
    abstract val unsignedImage: DirectoryProperty

    @get:Input
    abstract val signingIdentity: Property<String>

    @get:Input
    abstract val notarizationProfileName: Property<String>

    @get:Inject
    abstract val execOperations: ExecOperations

    @TaskAction
    fun generate() {
        val imageDirectory = imageDirectory.get().asFile.toPath()
        val unsignedImage = unsignedImage.get().asFile.toPath()
        imageDirectory.toFile().deleteRecursively()
        unsignedImage.recursiveCopyTo(imageDirectory)
        execOperations.exec {
            it.commandLine("codesign", "--options", "runtime", "--sign", signingIdentity.get(), imageDirectory.pathString)
        }
        val zip = temporaryDir.toPath().resolve(imageDirectory.name.replace(".app", ".zip"))
        execOperations.exec {
            it.commandLine("/usr/bin/ditto", "-c", "-k", "--keepParent", imageDirectory.pathString, zip.pathString)
        }
        execOperations.exec {
            it.commandLine("xcrun", "notarytool", "submit", zip.pathString, "--wait", "--keychain-profile", notarizationProfileName.get())
        }
        execOperations.exec {
            it.commandLine("xcrun", "stapler", "staple", imageDirectory.pathString)
        }
    }
}