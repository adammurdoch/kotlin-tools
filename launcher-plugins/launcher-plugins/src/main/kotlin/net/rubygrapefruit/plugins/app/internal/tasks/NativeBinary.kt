package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.download.DownloadRepository
import net.rubygrapefruit.plugins.app.NativeMachine
import net.rubygrapefruit.plugins.app.internal.currentOs
import net.rubygrapefruit.plugins.bootstrap.Versions
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.ProcessOperations
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.net.URI
import javax.inject.Inject
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

abstract class NativeBinary : DefaultTask() {
    @get:OutputFile
    abstract val launcherFile: RegularFileProperty

    @get:Input
    abstract val module: Property<String>

    @get:Input
    abstract val mainClass: Property<String>

    @get:InputFiles
    abstract val modulePath: ConfigurableFileCollection

    @get:Inject
    abstract val processOperations: ProcessOperations

    @TaskAction
    fun install() {
        val repository = DownloadRepository()
        val args = when (currentOs.machine) {
            NativeMachine.LinuxX64 -> {
                val baseName = "graalvm-jdk-${Versions.java}_linux-x64"
                Args(
                    URI("https://download.oracle.com/graalvm/${Versions.java}/latest/${baseName}_bin.tar.gz"),
                    baseName
                )
            }

            NativeMachine.MacOSX64 -> {
                val baseName = "graalvm-jdk-${Versions.java}_macos-x64"
                Args(
                    URI("https://download.oracle.com/graalvm/${Versions.java}/latest/${baseName}_bin.tar.gz"),
                    baseName
                )
            }

            NativeMachine.MacOSArm64 -> {
                val baseName = "graalvm-jdk-${Versions.java}_macos-aarch64"
                Args(
                    URI("https://download.oracle.com/graalvm/${Versions.java}/latest/${baseName}_bin.tar.gz"),
                    baseName
                )
            }

            else -> TODO()
        }

        val dir = repository.install(args.distribution, args.installName)
        val binDir = dir.listDirectoryEntries().first { it.isDirectory() }.resolve("Contents/Home/bin")
        require(binDir.isDirectory())

        val nativeImage = binDir.resolve("native-image")
        processOperations.exec { spec ->
            spec.commandLine(
                nativeImage,
                "-o", launcherFile.get().asFile.absolutePath,
                "--no-fallback",
                "--module-path", modulePath.asPath,
                "--module", "${module.get()}/${mainClass.get()}"
            )
        }
    }

    private class Args(val distribution: URI, val installName: String)
}