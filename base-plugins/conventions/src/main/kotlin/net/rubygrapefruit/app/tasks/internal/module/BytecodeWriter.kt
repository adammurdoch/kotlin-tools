package net.rubygrapefruit.app.tasks.internal.module

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.outputStream

internal class BytecodeWriter {
    fun writeTo(file: Path, builder: Builder.() -> Unit) {
        Files.createDirectories(file.parent)
        file.outputStream().buffered().use {
            val writer = BuilderImpl(it)
            builder(writer)
            writer.flush()
        }
    }

    interface Builder {
        fun module(name: String, exports: List<String>, requires: List<String>, requiresTransitive: List<String>)
    }

    private enum class AccessFlag(val value: Int) {
        Module(0x8000)
    }

    private sealed class ConstantPoolEntry(val index: Int) {
        abstract fun BuilderImpl.writeTo()
    }

    private class StringEntry(index: Int, val string: String) : ConstantPoolEntry(index) {
        override fun BuilderImpl.writeTo() {
            u1(1u)
            string(string)
        }
    }

    private class ClassInfoEntry(index: Int, val name: StringEntry) : ConstantPoolEntry(index) {
        override fun BuilderImpl.writeTo() {
            u1(7u)
            u2(name.index.toUInt())
        }
    }

    private class ModuleInfoEntry(index: Int, val name: StringEntry) : ConstantPoolEntry(index) {
        override fun BuilderImpl.writeTo() {
            u1(19u)
            u2(name.index.toUInt())
        }
    }

    private class PackageInfoEntry(index: Int, val name: StringEntry) : ConstantPoolEntry(index) {
        override fun BuilderImpl.writeTo() {
            u1(20u)
            u2(name.index.toUInt())
        }
    }

    private sealed class Attribute {
        abstract fun BuilderImpl.writeTo()
    }

    private class ModuleAttribute(
        val attributeName: StringEntry,
        val moduleInfo: ModuleInfoEntry,
        val javaBaseModuleInfo: ModuleInfoEntry,
        val javaVersion: StringEntry,
        val requires: List<ModuleInfoEntry>,
        val requiresTransitive: List<ModuleInfoEntry>,
        val exports: List<PackageInfoEntry>
    ) : Attribute() {
        override fun BuilderImpl.writeTo() {
            u2(attributeName.index.toUInt())
            val encoded = writing {
                u2(moduleInfo.index.toUInt())
                // access flags
                u2(0u)
                // module version
                u2(0u)
                // requires count
                u2((requires.size + requiresTransitive.size + 1).toUInt())
                u2(javaBaseModuleInfo.index.toUInt())
                u2(0x8000u)
                u2(javaVersion.index.toUInt())
                for (module in requires) {
                    u2(module.index.toUInt())
                    u2(0x8000u)
                    u2(0u)
                }
                for (module in requiresTransitive) {
                    u2(module.index.toUInt())
                    u2(0x8020u)
                    u2(0u)
                }
                // exports count
                u2(exports.size.toUInt())
                for (export in exports) {
                    u2(export.index.toUInt())
                    u2(0x8000u)
                    u2(0u)
                }
                // opens count
                u2(0u)
                // uses count
                u2(0u)
                // provides count
                u2(0u)
            }
            u4(encoded.size.toUInt())
            bytes(encoded)
        }
    }

    private class ConstantPool {
        val entries = mutableListOf<ConstantPoolEntry>()

        fun string(text: String): StringEntry {
            val entry = StringEntry(entries.size + 1, text)
            entries.add(entry)
            return entry
        }

        fun classInfo(name: StringEntry): ClassInfoEntry {
            val entry = ClassInfoEntry(entries.size + 1, name)
            entries.add(entry)
            return entry
        }

        fun classInfo(name: String): ClassInfoEntry {
            return classInfo(string(name))
        }

        fun moduleInfo(name: StringEntry): ModuleInfoEntry {
            val entry = ModuleInfoEntry(entries.size + 1, name)
            entries.add(entry)
            return entry
        }

        fun moduleInfo(name: String): ModuleInfoEntry {
            return moduleInfo(string(name))
        }

        fun packageInfo(name: StringEntry): PackageInfoEntry {
            val entry = PackageInfoEntry(entries.size + 1, name)
            entries.add(entry)
            return entry
        }

        fun packageInfo(name: String): PackageInfoEntry {
            return packageInfo(string(name))
        }

        fun BuilderImpl.writeTo() {
            val entries = entries
            u2(entries.size.toUInt() + 1u)
            for (entry in entries) {
                entry.apply { writeTo() }
            }
        }
    }

    private class BuilderImpl(
        stream: OutputStream
    ) : Builder {
        private val output = DataOutputStream(stream)

        override fun module(name: String, exports: List<String>, requires: List<String>, requiresTransitive: List<String>) {
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
            val kotlinModuleInfo = constantPool.moduleInfo("kotlin.stdlib")
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
                listOf(kotlinModuleInfo) + requiresModuleInfo,
                requiresTransitiveModuleInfo,
                exportsPackageInfo
            )
            val attributes = listOf(module)
            u2(attributes.size.toUInt())
            for (attribute in attributes) {
                attribute.apply { writeTo() }
            }
        }

        fun writing(builder: BuilderImpl.() -> Unit): ByteArray {
            val stream = ByteArrayOutputStream()
            val nested = BuilderImpl(stream)
            builder(nested)
            nested.flush()
            return stream.toByteArray()
        }

        fun accessFlags(vararg flags: AccessFlag) {
            val allFlags = flags.fold(0) { value, flag -> value.or(flag.value) }
            u2(allFlags.toUInt())
        }

        fun string(string: String) {
            output.writeUTF(string)
        }

        fun bytes(bytes: ByteArray) {
            output.write(bytes)
        }

        fun u1(value: UByte) {
            output.writeByte(value.toInt())
        }

        fun u2(value: UInt) {
            output.writeShort(value.toInt())
        }

        fun u4(value: UInt) {
            output.writeInt(value.toInt())
        }

        fun flush() {
            output.flush()
        }
    }
}
