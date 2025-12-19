import io.github.wasabithumb.jtoml.JToml
import io.github.wasabithumb.jtoml.value.table.TomlTable
import org.gradle.internal.extensions.stdlib.capitalized
import java.io.PrintWriter
import java.io.Writer
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
        languageVersion = JavaLanguageVersion.of(11)
    }
}

gradlePlugin {
    plugins {
        create("buildConstantsPlugin") {
            id = "net.rubygrapefruit.plugins.stage0.build-constants"
            implementationClass = "net.rubygrapefruit.plugins.stage0.BuildConstantsPlugin"
        }
        create("JavaGradlePlugin") {
            id = "net.rubygrapefruit.plugins.stage0.java-gradle-plugin"
            implementationClass = "net.rubygrapefruit.plugins.stage0.JavaGradlePluginPlugin"
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
            Generator(writer).generate {
                packageDecl(packageName)
                classDecl("BuildConstants", "public") {
                    typedFieldDecl("constants", "public static final", "BuildConstants")
                    constants(document.asTable(), null)
                    stage(0) {
                        typedConstants("buildConstants") {
                            stringFieldDecl("coordinates", "public final", "stage0:build-constants-plugins:0.0")
                            stringFieldDecl("pluginId", "public final", "net.rubygrapefruit.plugins.stage0.build-constants")
                        }
                    }
                    stage(1) {
                        typedConstants("plugins") {
                            stringFieldDecl("group", "public final", "stage1")
                            stringFieldDecl("coordinates", "public final", "stage1:gradle-plugin-plugin:0.0")
                            typedConstants("gradlePlugin") {
                                stringFieldDecl("id", "public final", "net.rubygrapefruit.plugins.stage1.gradle-plugin")
                            }
                        }
                    }
                    stage(2) {
                        typedConstants("plugins") {
                            typedConstants("gradlePlugin") {
                                stringFieldDecl("id", "public final", "net.rubygrapefruit.plugins.stage2.gradle-plugin")
                            }
                        }
                    }
                    typedConstants("production") {
                        typedConstants("plugins") {
                            stringFieldDecl("group", "public final", "net.rubygrapefruit.plugins")
                        }
                        typedConstants("bootstrapPlugins") {
                            stringFieldDecl("coordinates", "public final", "net.rubygrapefruit.plugins:bootstrap-plugins:0.0")
                        }
                    }
                }
            }
        }
    }

    private fun Generator.constants(container: TomlTable, parentVersion: String?) {
        val versionKeys = container.keys(false).filter { it.last() == "version" }
        val versionKey = versionKeys.firstOrNull()
        val version = if (versionKey != null) container.get(versionKey)!!.asPrimitive().asString() else parentVersion
        val keys = versionKeys + container.keys(false).filter { it.last() != "version" }
        for (key in keys) {
            val value = container.get(key)!!
            if (value.isPrimitive) {
                val primitiveValue = value.asPrimitive()
                val name = key.last()
                if (name == "coordinatesBase") {
                    stringFieldDecl("coordinates", "public final", "${primitiveValue.asString()}:$version")
                } else if (primitiveValue.isInteger) {
                    intFieldDecl(name, "public final", primitiveValue.asInteger())
                } else {
                    stringFieldDecl(name, "public final", primitiveValue.asString())
                }
            } else if (value.isTable) {
                val table = value.asTable()
                val varName = key.last()
                typedConstants(varName) {
                    constants(table, version)
                }
            }
        }
    }

    private fun Generator.stage(number: Int, body: Generator.() -> Unit) {
        typedConstants("stage$number", body)
    }

    private fun Generator.typedConstants(varName: String, body: Generator.() -> Unit) {
        val className = varName.capitalized() + "Constants"
        typedFieldDecl(varName, "public final", className)
        classDecl(className, "public static", body)
    }

    private class Generator(writer: Writer) {
        private val writer = PrintWriter(writer)
        private var indent = 0

        fun generate(body: Generator.() -> Unit) {
            writer.println("// Generated file - do not edit")
            body()
            writer.flush()
        }

        fun packageDecl(name: String) {
            appendLine {
                print("package ")
                print(name)
                print(";")
            }
        }

        fun classDecl(name: String, modifiers: String, body: Generator.() -> Unit) {
            appendLine {
                print(modifiers)
                print(" class ")
                print(name)
                print(" {")
            }
            indent++
            body()
            indent--
            appendLine {
                print("}")
            }
        }

        fun stringFieldDecl(name: String, modifiers: String, value: String) {
            appendLine {
                print(modifiers)
                print(" String ")
                print(name)
                print(" = \"")
                print(value)
                print("\";")
            }
        }

        fun intFieldDecl(name: String, modifiers: String, value: Int) {
            appendLine {
                print(modifiers)
                print(" int ")
                print(name)
                print(" = ")
                print(value)
                print(";")
            }
        }

        // type field = new type();
        fun typedFieldDecl(name: String, modifiers: String, type: String) {
            appendLine {
                print(modifiers)
                print(" ")
                print(type)
                print(" ")
                print(name)
                print(" = new ")
                print(type)
                print("();")
            }
        }

        fun appendLine(body: PrintWriter.() -> Unit) {
            append {
                repeat(indent) {
                    print("    ")
                }
                body()
                println()
            }
        }

        fun append(body: PrintWriter.() -> Unit) {
            writer.body()
        }
    }
}