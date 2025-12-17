import io.github.wasabithumb.jtoml.JToml
import java.io.PrintWriter
import kotlin.io.path.bufferedWriter
import kotlin.io.path.createDirectories

plugins {
    id("java-gradle-plugin")
}
buildscript {
    dependencies {
        classpath("io.github.wasabithumb:jtoml:1.3.0")
    }
}

group = "stage0"

gradlePlugin {
    plugins {
        create("buildConstantsPlugin") {
            id = "net.rubygrapefruit.plugins.stage0.build-constants"
            implementationClass = "net.rubygrapefruit.plugins.stage0.BuildConstantsPlugin"
        }
    }
}

val generateSource = tasks.register("generateConstants", GenerateSource::class.java) {
    versionsFile = file("../../versions.toml")
    outputDirectory = layout.buildDirectory.dir("generated-source/Constants.java")
}

sourceSets.main.configure {
    java.srcDir(generateSource.map { it.outputDirectory })
}

abstract class GenerateSource : DefaultTask() {
    @get:InputFile
    abstract val versionsFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val document = JToml.jToml().read(versionsFile.get().asFile)
        val kotlinVersion = document.get("kotlin.version")
        if (kotlinVersion == null || kotlinVersion.isPrimitive.not()) {
            throw IllegalArgumentException("Unexpected 'kotlin.version' property: $kotlinVersion")
        }

        val outputDir = outputDirectory.get().asFile.toPath()
        val packageName = "net.rubygrapefruit.plugins.stage0"
        val packageDir = outputDir.resolve(packageName.replace('.', '/'))
        packageDir.createDirectories()

        val sourceFile = packageDir.resolve("BuildConstants.java")
        sourceFile.bufferedWriter().use { writer ->
            PrintWriter(writer).run {
                println("// Generated file - do not edit")
                println("package $packageName;")
                println()
                println("public class BuildConstants {")
                println("    public static final KotlinConstants kotlin = new KotlinConstants();")
                println("    public static class KotlinConstants {")
                print("        public final String version = \"")
                print(kotlinVersion.asPrimitive().asString())
                println("\";")
                println("    }")
                println("    public static final Stage0Constants stage0 = new Stage0Constants();")
                println("    public static class Stage0Constants {")
                println("        public final String buildConstantsCoordinates = \"stage0:build-constants-plugin:0.0\";")
                println("    }")
                println("}")
                flush()
            }
        }
    }
}