package net.rubygrapefruit.bytecode

import java.io.InputStream

class BytecodeReader {
    /**
     * Reads class file to the given file.
     */
    fun readFrom(stream: InputStream, visitor: Visitor) {
        val decoder = StreamBackedDecoder(stream)
        decoder.doRead(visitor)
    }

    private fun Decoder.doRead(visitor: Visitor) {
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
        val module = AccessFlag.Module.value.and(access) != 0u
        val thisClass = constantPool.classInfo(u2())
        u2() // super class

        if (!module) {
            visitor.type(thisClass.name.replace("/", "."))
        }

        val interfaces = u2()
        for (i in 1..interfaces.toInt()) {
            u2()
        }
        if (module && interfaces != 0u) {
            throw IllegalArgumentException("Expected zero interfaces, found $interfaces")
        }

        val fields = u2()
        for (i in 1..fields.toInt()) {
            u2() // access
            u2() // name index
            u2() // descriptor index
            skipAttributes()
        }
        if (module && fields != 0u) {
            throw IllegalArgumentException("Expected zero fields, found $fields")
        }

        val methods = u2()
        for (i in 1..methods.toInt()) {
            u2() // access
            u2() // name index
            u2() // descriptor index
            skipAttributes()
        }
        if (module && methods != 0u) {
            throw IllegalArgumentException("Expected zero methods, found $methods")
        }

        val attributes = u2()
        for (i in 1..attributes.toInt()) {
            val nameIndex = u2()
            val length = u4()
            val attributeName = constantPool.string(nameIndex).string
            if (attributeName == "Module") {
                val moduleNameIndex = u2()
                val moduleName = constantPool.moduleInfo(moduleNameIndex).name
                visitor.module(moduleName)
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

    interface Visitor {
        fun module(name: String) {}

        fun type(name: String) {}
    }
}