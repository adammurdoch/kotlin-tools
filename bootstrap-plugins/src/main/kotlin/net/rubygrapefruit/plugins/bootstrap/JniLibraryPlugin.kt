package net.rubygrapefruit.plugins.bootstrap

import net.rubygrapefruit.plugins.app.Versions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.tasks.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion

class JniLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("java-library")

            repositories.mavenCentral()

            group = Versions.libs.group

            val java = extensions.getByType(JavaPluginExtension::class.java)
            java.toolchain.languageVersion.set(JavaLanguageVersion.of(Versions.plugins.java))

            val compile = tasks.withType(JavaCompile::class.java).named("compileJava") {
                it.options.headerOutputDirectory.set(layout.buildDirectory.dir("headers"))
            }
            val intel = tasks.register("nativeLibraryX64", CompileNative::class.java) {
                it.architecture.set("x86_64")
                it.headerDirectories.from(compile.map { it.options.headerOutputDirectory })
                it.sourceFiles.from(layout.projectDirectory.dir("src/main/c").asFileTree)
                it.sharedLibrary.set(layout.buildDirectory.file("native/cpu-info-x64.dylib"))
            }
            val apple = tasks.register("nativeLibraryArm64", CompileNative::class.java) {
                it.architecture.set("arm64")
                it.headerDirectories.from(compile.map { it.options.headerOutputDirectory })
                it.sourceFiles.from(layout.projectDirectory.dir("src/main/c").asFileTree)
                it.sharedLibrary.set(layout.buildDirectory.file("native/cpu-info-arm64.dylib"))
            }
            tasks.register("nativeLibrary") {
                it.dependsOn(intel)
                it.dependsOn(apple)
            }
            if (System.getProperty("os.name").contains("Mac OS X")) {
                tasks.withType(Jar::class.java).named("jar") {
                    it.from(intel.flatMap { it.sharedLibrary })
                    it.from(apple.flatMap { it.sharedLibrary })
                }
            }
        }
    }
}