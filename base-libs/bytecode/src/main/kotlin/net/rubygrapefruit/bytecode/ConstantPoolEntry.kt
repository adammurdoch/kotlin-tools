package net.rubygrapefruit.bytecode

internal sealed class ConstantPoolEntry(val index: Int) {
    abstract fun Encoder.writeTo()
}

internal class StringEntry(index: Int, val string: String) : ConstantPoolEntry(index) {
    override fun toString(): String {
        return "CONSTANT_Utf8_info($index: $string)"
    }

    override fun Encoder.writeTo() {
        u1(1u)
        string(string)
    }
}

internal class ClassInfoEntry(index: Int, private val nameIndex: UInt, private val owner: ConstantPool) : ConstantPoolEntry(index) {
    val name by lazy { owner.string(nameIndex).string }

    override fun toString(): String {
        return "CONSTANT_Class_info($index: name=$nameIndex)"
    }

    override fun Encoder.writeTo() {
        u1(7u)
        u2(nameIndex)
    }
}

internal class ModuleInfoEntry(index: Int, val nameIndex: UInt, private val owner: ConstantPool) : ConstantPoolEntry(index) {
    val name by lazy { owner.string(nameIndex).string }

    override fun toString(): String {
        return "CONSTANT_Module_info($index: name=$nameIndex)"
    }

    override fun Encoder.writeTo() {
        u1(19u)
        u2(nameIndex)
    }
}

internal class PackageInfoEntry(index: Int, val nameIndex: UInt, private val owner: ConstantPool) : ConstantPoolEntry(index) {
    val name by lazy { owner.string(nameIndex).string }

    override fun toString(): String {
        return "CONSTANT_Package_info($index: name=$nameIndex)"
    }

    override fun Encoder.writeTo() {
        u1(20u)
        u2(nameIndex)
    }
}

