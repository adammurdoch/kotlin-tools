package net.rubygrapefruit.bytecode

import java.io.InputStream

/**
 * Reads the contents of a class file.
 */
class BytecodeReader {
    /**
     * Reads class file from the given stream.
     */
    fun readFrom(stream: InputStream, visitor: ClassFileVisitor) {
        val decoder = StreamBackedDecoder(stream)
        decoder.doRead(visitor)
    }

    private fun Decoder.doRead(visitor: ClassFileVisitor) {
        val header = u4()
        if (header != 0xCAFEBABEu) {
            throw IllegalArgumentException("Unexpected magic number: $header")
        }
        u2() // minor version
        val major = u2()
        if (major > 62u) {
            throw IllegalArgumentException("Unrecognized major version: $major")
        }
        val constantPool = ConstantPool()
        constantPool.run { readFrom() }

        val access = u2()
        val module = AccessFlag.Module.containedIn(access)
        val thisClass = constantPool.classInfo(u2())
        val superClassIndex = u2()
        val superClass = if (superClassIndex == 0u) null else constantPool.classInfo(superClassIndex)

        val interfaceCount = u2()
        val interfaces = mutableListOf<String>()
        for (i in 1..interfaceCount.toInt()) {
            val nameIndex = u2()
            interfaces.add(constantPool.classInfo(nameIndex).typeName)
        }
        if (module && interfaceCount != 0u) {
            throw IllegalArgumentException("Expected zero interfaces, found $interfaceCount")
        }

        if (!module) {
            when {
                AccessFlag.Interface.containedIn(access) -> visitor.type(InterfaceInfo(thisClass.typeName, superClass?.typeName, interfaces))
                AccessFlag.Annotation.containedIn(access) -> visitor.type(AnnotationInfo(thisClass.typeName, superClass?.typeName, interfaces))
                AccessFlag.Enum.containedIn(access) -> visitor.type(EnumInfo(thisClass.typeName, superClass?.typeName, interfaces))
                else -> visitor.type(ClassInfo(thisClass.typeName, superClass?.typeName, interfaces))
            }
        }

        val fieldCount = u2()
        for (i in 1..fieldCount.toInt()) {
            u2() // access
            u2() // name index
            u2() // descriptor index
            skipAttributes()
        }
        if (module && fieldCount != 0u) {
            throw IllegalArgumentException("Expected zero fields, found $fieldCount")
        }

        val methodCount = u2()
        for (i in 1..methodCount.toInt()) {
            u2() // access
            u2() // name index
            u2() // descriptor index
            skipAttributes()
        }
        if (module && methodCount != 0u) {
            throw IllegalArgumentException("Expected zero methods, found $methodCount")
        }

        val attributes = u2()
        for (i in 1..attributes.toInt()) {
            val nameIndex = u2()
            val length = u4()
            val attributeName = constantPool.string(nameIndex).string
            if (attributeName == "Module") {
                val moduleNameIndex = u2()
                val moduleName = constantPool.moduleInfo(moduleNameIndex).name
                visitor.module(ModuleInfo(moduleName))
                skip(length.toInt() - 2)
                break
            } else {
                skip(length.toInt())
            }
        }
    }

    private fun Decoder.skipAttributes() {
        val attributes = u2()
        for (i in 1..attributes.toInt()) {
            val nameIndex = u2()
            val length = u4()
            skip(length.toInt())
        }
    }

}