@file:OptIn(ExperimentalSerializationApi::class)

package net.rubygrapefruit.plugins.app.internal.tasks

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.rubygrapefruit.bytecode.*
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

abstract class RuntimeModulePath : DefaultTask() {
    @get:InputFiles
    abstract val classpath: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputFile
    abstract val inferredModulesFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val dir = outputDirectory.get().asFile
        dir.deleteRecursively()
        dir.mkdirs()

        val modules = inferredModulesFile.get().asFile.inputStream().use { Json.decodeFromStream<Modules>(it) }
        val modulesByFile = (modules.modules).associateBy { it.fileName }

        val bytecodeWriter = BytecodeWriter()
        val bytecodeReader = BytecodeReader()

        for (file in classpath) {
            val module = modulesByFile.getValue(file.name)
            val dest = dir.resolve(file.name)
            if (module.automatic) {
                patchJarFile(file, dest, bytecodeReader, bytecodeWriter, module)
            } else {
                file.copyTo(dest)
            }
        }
    }

    private fun patchJarFile(file: File, dest: File, bytecodeReader: BytecodeReader, bytecodeWriter: BytecodeWriter, module: InferredModule) {
        info("* patch ${file.name}")
        file.inputStream().use { instr ->
            val zipInstr = ZipInputStream(instr)
            val packages = mutableSetOf<String>()
            dest.outputStream().use { outstr ->
                val zipOutstr = ZipOutputStream(outstr)
                while (true) {
                    val entry = zipInstr.nextEntry
                    if (entry == null) {
                        break
                    }
                    zipOutstr.putNextEntry(entry)
                    if (entry.name.endsWith(".class")) {
                        val stream = CopyingInputStream(zipInstr, zipOutstr)
                        bytecodeReader.readFrom(stream, object : ClassFileVisitor {
                            override fun type(type: TypeInfo): TypeVisitor? {
                                val packageName = type.name.substringBeforeLast(".", "")
                                if (packageName.isNotEmpty()) {
                                    packages.add(packageName)
                                }
                                return null
                            }
                        })
                        stream.finish()
                    } else {
                        zipInstr.copyTo(zipOutstr)
                    }
                    zipOutstr.closeEntry()
                }
                zipOutstr.putNextEntry(ZipEntry("module-info.class"))
                info("  * exports")
                for (packageName in packages) {
                    info("    * $packageName")
                }
                info("  * requires")
                for (requires in module.requires) {
                    info("    * $requires")
                }
                bytecodeWriter.writeTo(zipOutstr) {
                    module(module.name, packages.toList(), module.requires, emptyList())
                }
                zipOutstr.closeEntry()
                zipOutstr.finish()
            }
        }
    }

    private fun info(message: String) {
        logger.info(message)
    }

    private class CopyingInputStream(val inputStream: InputStream, val outputStream: OutputStream) : InputStream() {
        override fun read(): Int {
            val b = inputStream.read()
            if (b >= 0) {
                outputStream.write(b)
            }
            return b
        }

        override fun read(buffer: ByteArray, offset: Int, count: Int): Int {
            val nread = inputStream.read(buffer, offset, count)
            if (nread >= 0) {
                outputStream.write(buffer, offset, nread)
            }
            return nread
        }

        fun finish() {
            val buffer = ByteArray(4096)
            while (read(buffer) >= 0) {
                //
            }
        }
    }
}