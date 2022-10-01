package net.rubygrapefruit.bytecode

interface ClassFileVisitor {
    fun module(info: ModuleInfo) {}

    fun type(info: TypeInfo) {}
}

class ModuleInfo(val name: String)

class TypeInfo(val name: String, val superClassName: String?, val interfaces: List<String>)
