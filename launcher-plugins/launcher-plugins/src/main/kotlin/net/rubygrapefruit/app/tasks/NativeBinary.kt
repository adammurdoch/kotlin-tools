package net.rubygrapefruit.app.tasks

import net.rubygrapefruit.app.NativeMachine
import net.rubygrapefruit.app.internal.currentOs
import net.rubygrapefruit.download.DownloadRepository
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
            NativeMachine.LinuxX64 -> Args(
                URI("https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.2.0/graalvm-ce-java11-linux-amd64-22.2.0.tar.gz"),
                "graalvm-ce-java11-linux-amd64-22.2.0",
                "graalvm-ce-java11-22.2.0/bin",
                "linux-amd64"
            )
            NativeMachine.MacOSX64, NativeMachine.MacOSArm64 -> Args(
                URI("https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-22.2.0/graalvm-ce-java11-darwin-amd64-22.2.0.tar.gz"),
                "graalvm-ce-java11-darwin-amd64-22.2.0",
                "graalvm-ce-java11-22.2.0/Contents/Home/bin",
                "darwin-amd64"
            )
            else -> TODO()
        }

        val dir = repository.install(args.distribution, args.installName) { dir ->
            val tool = dir.resolve("${args.binDirPath}/gu")
            processOperations.exec { spec ->
                spec.commandLine(tool, "install", "native-image")
            }
        }

        val nativeImage = dir.resolve("${args.binDirPath}/native-image")
        processOperations.exec { spec ->
            spec.commandLine(
                nativeImage,
                "-o", launcherFile.get().asFile.absolutePath,
                "--target=${args.target}",
                "--no-fallback",
                "--module-path", modulePath.asPath,
                "--module", "${module.get()}/${mainClass.get()}"
            )
        }
    }

    private class Args(val distribution: URI, val installName: String, val binDirPath: String, val target: String)
}