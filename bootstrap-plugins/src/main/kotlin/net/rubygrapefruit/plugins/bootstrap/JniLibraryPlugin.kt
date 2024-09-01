package net.rubygrapefruit.plugins.bootstrap

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile

class JniLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("java-library")
            val compile = tasks.withType(JavaCompile::class.java).named("compileJava") {
                it.options.headerOutputDirectory.set(layout.buildDirectory.dir("headers"))
            }
            val intel = tasks.register("nativeLibraryX64", CompileNative::class.java) {
                it.architecture.set("x86_64")
                it.headerDirectories.from(compile.map { it.options.headerOutputDirectory })
                it.sourceFiles.from(layout.projectDirectory.dir("src/main/c").asFileTree)
                it.sharedLibrary.set(layout.buildDirectory.file("native/x64/arch.dylib"))
            }
            val apple = tasks.register("nativeLibraryArm64", CompileNative::class.java) {
                it.architecture.set("arm64")
                it.headerDirectories.from(compile.map { it.options.headerOutputDirectory })
                it.sourceFiles.from(layout.projectDirectory.dir("src/main/c").asFileTree)
                it.sharedLibrary.set(layout.buildDirectory.file("native/arm64/arch.dylib"))
            }
            tasks.register("nativeLibrary") {
                it.dependsOn(intel)
                it.dependsOn(apple)
            }
        }
    }
}