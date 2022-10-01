package net.rubygrapefruit.bytecode

internal class ConstantPool {
    private val entries = mutableListOf<ConstantPoolEntry>()

    fun entry(index: UInt): ConstantPoolEntry {
        return entries[index.toInt() - 1]
    }

    fun string(text: String): StringEntry {
        val entry = StringEntry(entries.size + 1, text)
        entries.add(entry)
        return entry
    }

    fun string(index: UInt): StringEntry {
        return entry(index) as StringEntry
    }

    fun classInfo(name: StringEntry): ClassInfoEntry {
        val entry = ClassInfoEntry(entries.size + 1, name.index.toUInt(), this)
        entries.add(entry)
        return entry
    }

    fun classInfo(name: String): ClassInfoEntry {
        return classInfo(string(name))
    }

    fun classInfo(index: UInt): ClassInfoEntry {
        return entry(index) as ClassInfoEntry
    }

    fun moduleInfo(name: StringEntry): ModuleInfoEntry {
        val entry = ModuleInfoEntry(entries.size + 1, name.index.toUInt(), this)
        entries.add(entry)
        return entry
    }

    fun moduleInfo(name: String): ModuleInfoEntry {
        return moduleInfo(string(name))
    }

    fun moduleInfo(index: UInt): ModuleInfoEntry {
        return entry(index) as ModuleInfoEntry
    }

    fun packageInfo(name: StringEntry): PackageInfoEntry {
        val entry = PackageInfoEntry(entries.size + 1, name.index.toUInt(), this)
        entries.add(entry)
        return entry
    }

    fun packageInfo(name: String): PackageInfoEntry {
        return packageInfo(string(name))
    }

    fun Encoder.writeTo() {
        val entries = entries
        u2(entries.size.toUInt() + 1u)
        for (entry in entries) {
            entry.apply { writeTo() }
        }
    }

    fun Decoder.readFrom() {
        val count = u2().toInt() - 1
        for (index in 1..count) {
            val tag = u1().toInt()
            val entry = when (tag) {
                1 -> {
                    val text = string()
                    StringEntry(index, text)
                }
                3 -> {
                    val value = i4()
                    IntegerEntry(index, value)
                }
                7 -> {
                    val nameIndex = u2()
                    ClassInfoEntry(index, nameIndex, this@ConstantPool)
                }
                8 -> {
                    val textEntry = u2()
                    StringInfoEntry(index, textEntry)
                }
                9 -> {
                    val classIndex = u2()
                    val nameAndType = u2()
                    FieldRefEntry(index, classIndex, nameAndType)
                }
                10 -> {
                    val classIndex = u2()
                    val nameAndType = u2()
                    MethodRefEntry(index, classIndex, nameAndType)
                }
                12 -> {
                    val nameIndex = u2()
                    val descriptorIndex = u2()
                    NameAndTypeEntry(index, nameIndex, descriptorIndex)
                }
                19 -> {
                    val nameIndex = u2()
                    ModuleInfoEntry(index, nameIndex, this@ConstantPool)
                }
                20 -> {
                    val nameIndex = u2()
                    PackageInfoEntry(index, nameIndex, this@ConstantPool)
                }
                else -> {
                    throw IllegalArgumentException("Unrecognized constant pool tag: $tag")
                }
            }
            entries.add(entry)
        }
    }
}