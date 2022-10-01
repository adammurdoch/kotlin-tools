package net.rubygrapefruit.bytecode

internal sealed class ConstantPoolEntry(val index: Int) {
    abstract fun Encoder.writeTo()
}

internal class UnusableEntry(index: Int) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
    }
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

internal class IntegerEntry(index: Int, val value: Int) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(3u)
        i4(value)
    }
}

internal class LongEntry(index: Int, val highValue: UInt, val lowValue: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(5u)
        u4(highValue)
        u4(lowValue)
    }
}

internal class ClassInfoEntry(index: Int, private val nameIndex: UInt, private val owner: ConstantPool) : ConstantPoolEntry(index) {
    val name by lazy { owner.string(nameIndex).string }

    val typeName: String
        get() = name.replace("/", ".")

    override fun toString(): String {
        return "CONSTANT_Class_info($index: name=$nameIndex)"
    }

    override fun Encoder.writeTo() {
        u1(7u)
        u2(nameIndex)
    }
}

internal class StringInfoEntry(index: Int, private val textIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(8u)
        u2(textIndex)
    }
}

internal class FieldRefEntry(index: Int, private val classIndex: UInt, private val nameAndTypeIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(9u)
        u2(classIndex)
        u2(nameAndTypeIndex)
    }
}

internal class MethodRefEntry(index: Int, private val classIndex: UInt, private val nameAndTypeIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(10u)
        u2(classIndex)
        u2(nameAndTypeIndex)
    }
}

internal class InterfaceMethodRefEntry(index: Int, private val classIndex: UInt, private val nameAndTypeIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(11u)
        u2(classIndex)
        u2(nameAndTypeIndex)
    }
}

internal class NameAndTypeEntry(index: Int, private val nameIndex: UInt, private val descriptorIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(12u)
        u2(nameIndex)
        u2(descriptorIndex)
    }
}

internal class MethodTypeInfo(index: Int, private val descriptorIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(16u)
        u2(descriptorIndex)
    }
}

internal class InvokeDynamicInfo(index: Int, private val bootstrapMethodIndex: UInt, private val nameAndTypeIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(18u)
        u2(bootstrapMethodIndex)
        u2(nameAndTypeIndex)
    }
}

internal class MethodHandleInfo(index: Int, private val referenceType: UByte, private val referenceIndex: UInt) : ConstantPoolEntry(index) {
    override fun Encoder.writeTo() {
        u1(15u)
        u1(referenceType)
        u2(referenceIndex)
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

