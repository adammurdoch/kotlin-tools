package net.rubygrapefruit.bytecode

import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.outputStream

class BytecodeWriter {
    /**
     * Generates a class file to the given file.
     */
    fun writeTo(file: Path, builder: Builder.() -> Unit) {
        Files.createDirectories(file.parent)
        file.outputStream().buffered().use {
            writeTo(it, builder)
        }
    }

    /**
     * Generates a class file to the given stream.
     */
    fun writeTo(outputStream: OutputStream, builder: Builder.() -> Unit) {
        val encoder = StreamBackedEncoder(outputStream)
        val writer = BuilderImpl(encoder)
        builder(writer)
        encoder.flush()
    }

    interface Builder {
        /**
         * Generates a module file.
         */
        fun module(name: String, exports: List<String>, requires: List<String>, requiresTransitive: List<String>)
    }

    private class BuilderImpl(
        private val output: Encoder
    ) : Builder {
        override fun module(name: String, exports: List<String>, requires: List<String>, requiresTransitive: List<String>) {
            output.module(name, exports, requires, requiresTransitive)
        }

        private fun Encoder.module(name: String, exports: List<String>, requires: List<String>, requiresTransitive: List<String>) {
            u4(0xCAFEBABEu)
            // bytecode version
            u2(0u)
            u2(55u)

            // constant pool
            val constantPool = ConstantPool()
            val classInfo = constantPool.classInfo("module-info")
            val moduleAttributeName = constantPool.string("Module")
            val moduleInfo = constantPool.moduleInfo(name)
            val javaBaseModuleInfo = constantPool.moduleInfo("java.base")
            val javaVersion = constantPool.string(System.getProperty("java.version"))
            val requiresModuleInfo = requires.map { constantPool.moduleInfo(it) }
            val requiresTransitiveModuleInfo = requiresTransitive.map { constantPool.moduleInfo(it) }
            val exportsPackageInfo = exports.map { constantPool.packageInfo(it.replace('.', '/')) }
            constantPool.apply { writeTo() }

            accessFlags(AccessFlag.Module)

            // this class and super class
            u2(classInfo.index.toUInt())
            u2(0u)

            // interfaces, fields, methods
            u2(0u)
            u2(0u)
            u2(0u)

            // attributes
            val module = ModuleAttribute(
                moduleAttributeName,
                moduleInfo,
                javaBaseModuleInfo,
                javaVersion,
                requiresModuleInfo,
                requiresTransitiveModuleInfo,
                exportsPackageInfo
            )
            val attributes = listOf(module)
            u2(attributes.size.toUInt())
            for (attribute in attributes) {
                attribute.apply { writeTo() }
            }
        }

        fun Encoder.accessFlags(vararg flags: AccessFlag) {
            val allFlags = flags.fold(0u) { value, flag -> value.or(flag.value) }
            u2(allFlags)
        }
    }
}
