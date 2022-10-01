package net.rubygrapefruit.bytecode

interface ClassFileVisitor {
    fun module(info: ModuleInfo) {}

    fun type(info: TypeInfo) {}
}

class ModuleInfo(val name: String)

sealed class TypeInfo(val name: String, val superClassName: String?, val interfaces: List<String>)

class ClassInfo(name: String, superClassName: String?, interfaces: List<String>) : TypeInfo(name, superClassName, interfaces)

class InterfaceInfo(name: String, superClassName: String?, interfaces: List<String>) : TypeInfo(name, superClassName, interfaces)

class EnumInfo(name: String, superClassName: String?, interfaces: List<String>) : TypeInfo(name, superClassName, interfaces)

class AnnotationInfo(name: String, superClassName: String?, interfaces: List<String>) : TypeInfo(name, superClassName, interfaces)
