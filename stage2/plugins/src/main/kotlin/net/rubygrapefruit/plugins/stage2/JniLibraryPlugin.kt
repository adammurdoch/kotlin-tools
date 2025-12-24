package net.rubygrapefruit.plugins.stage2

import net.rubygrapefruit.plugins.stage0.BuildConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion

@Suppress("unused")
class JniLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("java-library")
            plugins.apply(BuildConstants.constants.stage0.buildConstants.pluginId)

            repositories.mavenCentral()

            group = BuildConstants.constants.production.libraries.group

            val extension = extensions.create("library", JniLibrary::class.java)
            extension.targetJavaVersion.convention(BuildConstants.constants.libs.jvm.version)

            extension.cSourceDirs.from(layout.projectDirectory.dir("src/main/c"))

            val java = extensions.getByType(JavaPluginExtension::class.java)
            java.toolchain.languageVersion.set(extension.targetJavaVersion.map { JavaLanguageVersion.of(it) })

            val compile = tasks.withType(JavaCompile::class.java).named("compileJava") { task ->
                task.options.headerOutputDirectory.set(layout.buildDirectory.dir("headers"))
            }
            val intel = tasks.register("nativeLibraryX64", CompileNative::class.java) { task ->
                task.architecture.set("x86_64")
                task.headerDirectories.from(compile.map { it.options.headerOutputDirectory })
                task.sourceFiles.from(extension.cSourceDirs.asFileTree)
                task.sharedLibrary.set(layout.buildDirectory.file("native/cpu-info-x64.dylib"))
            }
            val apple = tasks.register("nativeLibraryArm64", CompileNative::class.java) { task ->
                task.architecture.set("arm64")
                task.headerDirectories.from(compile.map { it.options.headerOutputDirectory })
                task.sourceFiles.from(extension.cSourceDirs.asFileTree)
                task.sharedLibrary.set(layout.buildDirectory.file("native/cpu-info-arm64.dylib"))
            }
            tasks.register("nativeLibrary") { task ->
                task.dependsOn(intel)
                task.dependsOn(apple)
            }
            if (System.getProperty("os.name").contains("Mac OS X")) {
                tasks.withType(Jar::class.java).named("jar") { task ->
                    task.from(intel.flatMap { it.sharedLibrary })
                    task.from(apple.flatMap { it.sharedLibrary })
                }
            }
        }
    }
}