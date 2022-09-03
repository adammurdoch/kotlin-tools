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
        fun module(name: String)
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

    private sealed class Attribute() {
        abstract fun BuilderImpl.writeTo()
    }

    private class ModuleAttribute(
        val attributeName: StringEntry,
        val moduleInfo: ModuleInfoEntry,
        val javaBaseModuleInfo: ModuleInfoEntry,
        val javaVersion: StringEntry,
        val modules: List<ModuleInfoEntry>
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
                u2(modules.size.toUInt() + 1u)
                u2(javaBaseModuleInfo.index.toUInt())
                u2(0x8000u)
                u2(javaVersion.index.toUInt())
                for (module in modules) {
                    u2(module.index.toUInt())
                    u2(0x8000u)
                    u2(0u)
                }
                // exports count
                u2(0u)
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

    private class BuilderImpl(
        stream: OutputStream
    ) : Builder {
        private val output = DataOutputStream(stream)

        override fun module(name: String) {
            u4(0xCAFEBABEu)
            // version
            u2(0u)
            u2(55u)

            // constant pool
            val className = StringEntry(1, "module-info")
            val classInfo = ClassInfoEntry(2, className)
            val moduleAttributeName = StringEntry(3, "Module")
            val moduleName = StringEntry(4, name)
            val moduleInfo = ModuleInfoEntry(5, moduleName)
            val javaBaseName = StringEntry(6, "java.base")
            val javaBaseModuleInfo = ModuleInfoEntry(7, javaBaseName)
            val javaVersion = StringEntry(8, System.getProperty("java.version"))
            val kotlinName = StringEntry(9, "kotlin.stdlib")
            val kotlinModuleInfo = ModuleInfoEntry(10, kotlinName)
            val entries = listOf(className, classInfo, moduleAttributeName, moduleName, moduleInfo, javaBaseName, javaBaseModuleInfo, javaVersion, kotlinName, kotlinModuleInfo).sortedBy { it.index }
            u2(entries.size.toUInt() + 1u)
            for (entry in entries) {
                entry.apply { writeTo() }
            }

            accessFlags(AccessFlag.Module)

            // this class and super class
            u2(classInfo.index.toUInt())
            u2(0u)

            // interfaces, fields, methods
            u2(0u)
            u2(0u)
            u2(0u)

            // attributes
            val module = ModuleAttribute(moduleAttributeName, moduleInfo, javaBaseModuleInfo, javaVersion, listOf(kotlinModuleInfo))
            val attributes = listOf(module)
            u2(attributes.size.toUInt())
            for (attribute in attributes) {
                attribute.apply { writeTo() }
            }
        }

        val UInt.display: String get() = "Ox${toString(16).padStart(8, '0')} (${toString()})"

        val UByte.display: String get() = "Ox${toString(16).padStart(2, '0')} (${toString()})"

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
