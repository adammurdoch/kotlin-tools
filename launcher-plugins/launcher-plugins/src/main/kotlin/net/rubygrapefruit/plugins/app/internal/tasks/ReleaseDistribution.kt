package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.plugins.app.internal.copyDir
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import javax.inject.Inject
import kotlin.io.path.name
import kotlin.io.path.pathString

abstract class ReleaseDistribution : AbstractDistributionImage() {
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
        copyDir(unsignedImage, imageDirectory)

        val bundleDir = imageDirectory.resolve(rootDirPath.get()).parent

        execOperations.exec {
            it.commandLine("codesign", "--options", "runtime", "--sign", signingIdentity.get(), bundleDir.pathString)
        }
        val zip = temporaryDir.toPath().resolve(bundleDir.name.replace(".app", ".zip"))
        execOperations.exec {
            it.commandLine("/usr/bin/ditto", "-c", "-k", "--keepParent", bundleDir.pathString, zip.pathString)
        }
        execOperations.exec {
            it.commandLine("xcrun", "notarytool", "submit", zip.pathString, "--wait", "--keychain-profile", notarizationProfileName.get())
        }
        execOperations.exec {
            it.commandLine("xcrun", "stapler", "staple", bundleDir.pathString)
        }
    }
}