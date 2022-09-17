package net.rubygrapefruit.bytecode

import java.io.InputStream

class BytecodeReader {
    fun readFrom(stream: InputStream, visitor: Visitor) {
        val decoder = StreamBackedDecoder(stream)
        decoder.doRead(visitor)
    }

    private fun Decoder.doRead(visitor: Visitor) {
        val header = u4()
        if (header != 0xCAFEBABEu) {
            throw IllegalArgumentException("Unexpected magic number: $header")
        }
        val minor = u2()
        val major = u2()
        if (major > 62u) {
            throw IllegalArgumentException("Unrecognized major version: $major")
        }
        val constantPool = ConstantPool()
        constantPool.run { readFrom() }

        val access = u2()
        if (AccessFlag.Module.value.and(access) == 0u) {
            throw IllegalArgumentException("Not a module file")
        }
        u2() // this class
        u2() // super class

        val interfaces = u2()
        if (interfaces != 0u) {
            throw IllegalArgumentException("Expected zero interfaces, found $interfaces")
        }

        val fields = u2()
        if (fields != 0u) {
            throw IllegalArgumentException("Expected zero fields, found $fields")
        }

        val methods = u2()
        if (methods != 0u) {
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

    interface Visitor {
        fun module(name: String)
    }
}