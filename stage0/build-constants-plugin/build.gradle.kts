import io.github.wasabithumb.jtoml.JToml
import io.github.wasabithumb.jtoml.value.table.TomlTable
import org.gradle.internal.extensions.stdlib.capitalized
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

java {
    toolchain {
        version = JavaVersion.VERSION_17
    }
}

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
                println("    public static final BuildConstants constants = new BuildConstants();")
                constants(document.asTable(), "    ", null)
                println()
                println("    public final Stage0Constants stage0 = new Stage0Constants();")
                println("    public static class Stage0Constants {")
                println("        public final String buildConstantsCoordinates = \"stage0:build-constants-plugin:0.0\";")
                println("    }")
                println("}")
                flush()
            }
        }
    }

    private fun PrintWriter.constants(container: TomlTable, prefix: String, parentVersion: String?) {
        val versionKeys = container.keys(false).filter { it.last() == "version" }
        val versionKey = versionKeys.firstOrNull()
        val version = if (versionKey != null) container.get(versionKey)!!.asPrimitive().asString() else parentVersion
        val keys = versionKeys + container.keys(false).filter { it.last() != "version" }
        for (key in keys) {
            val value = container.get(key)!!
            if (value.isPrimitive) {
                val primitiveValue = value.asPrimitive()
                val name = key.last()
                print(prefix)
                if (name == "coordinatesBase") {
                    print("public final String coordinates = \"")
                    print(primitiveValue.asString())
                    print(":")
                    print(version)
                    println("\";")
                } else if (primitiveValue.isInteger) {
                    print("public final int ")
                    print(name)
                    print(" = ")
                    print(primitiveValue.asInteger())
                    println(";")
                } else {
                    print("public final String ")
                    print(name)
                    print(" = \"")
                    print(primitiveValue.asString())
                    println("\";")
                }
            } else if (value.isTable) {
                val table = value.asTable()
                val varName = key.last()
                val className = varName.capitalized() + "Constants"
                println()
                print(prefix)
                print("public final ")
                print(className)
                print(" ")
                print(varName)
                print(" = new ")
                print(className)
                println("();")
                print(prefix)
                print("public static class ")
                print(className)
                println(" {")
                constants(table, "$prefix    ", version)
                print(prefix)
                println("}")
            }
        }
    }
}