package net.rubygrapefruit.plugins.app.internal.tasks

import net.rubygrapefruit.download.DownloadRepository
import net.rubygrapefruit.plugins.app.internal.*
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
import kotlin.io.path.isRegularFile
import kotlin.io.path.listDirectoryEntries

abstract class NativeBinary : DefaultTask() {
    @get:OutputFile
    abstract val launcherFile: RegularFileProperty

    @get:Input
    abstract val module: Property<String>

    @get:Input
    abstract val mainClass: Property<String>

    @get:Input
    abstract val javaVersion: Property<Int>

    @get:InputFiles
    abstract val modulePath: ConfigurableFileCollection

    @get:Inject
    abstract val processOperations: ProcessOperations

    @TaskAction
    fun install() {
        val repository = DownloadRepository()
        val jdkVersion = javaVersion.get()
        val args = when (HostMachine.current) {
            LinuxX64 -> {
                val baseName = "graalvm-jdk-${jdkVersion}_linux-x64"
                Args(URI("https://download.oracle.com/graalvm/${jdkVersion}/latest/${baseName}_bin.tar.gz"), baseName, "bin")
            }

            MacOsX64 -> {
                val baseName = "graalvm-jdk-${jdkVersion}_macos-x64"
                Args(URI("https://download.oracle.com/graalvm/${jdkVersion}/latest/${baseName}_bin.tar.gz"), baseName, "Contents/Home/bin", binPrefix = listOf("arch", "-arch", "x86_64"))
            }

            MacOsArm64 -> {
                val baseName = "graalvm-jdk-${jdkVersion}_macos-aarch64"
                Args(URI("https://download.oracle.com/graalvm/${jdkVersion}/latest/${baseName}_bin.tar.gz"), baseName, "Contents/Home/bin", binPrefix = listOf("arch", "-arch", "arm64"))
            }

            WindowsX64 -> {
                val baseName = "graalvm-jdk-${jdkVersion}_windows-x64"
                Args(URI("https://download.oracle.com/graalvm/${jdkVersion}/latest/${baseName}_bin.zip"), baseName, "bin", ".cmd")
            }
        }

        val dir = repository.install(args.distribution, args.installName)
        val binDir = dir.listDirectoryEntries().first { it.isDirectory() }.resolve(args.binDirPath)
        require(binDir.isDirectory())

        val nativeImage = binDir.resolve("native-image${args.extension}")
        require(nativeImage.isRegularFile())

        processOperations.exec { spec ->
            spec.commandLine(
                args.binPrefix + listOf(
                    nativeImage,
                    "-o", launcherFile.get().asFile.absolutePath,
                    "--no-fallback",
                    "--module-path", modulePath.asPath,
                    "--module", "${module.get()}/${mainClass.get()}"
                )
            )
        }
    }

    private class Args(val distribution: URI, val installName: String, val binDirPath: String, val extension: String = "", val binPrefix: List<String> = emptyList())
}