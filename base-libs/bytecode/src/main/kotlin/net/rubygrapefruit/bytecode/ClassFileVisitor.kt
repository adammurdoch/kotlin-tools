package net.rubygrapefruit.bytecode

interface ClassFileVisitor {
    /**
     * Visits a module.
     */
    fun module(info: ModuleInfo) {}

    /**
     * Visits a type. Can return a `TypeVisitor` to receive information about the methods and fields of the type.
     */
    fun type(info: TypeInfo): TypeVisitor? = null
}

interface TypeVisitor {
    fun method(info: MethodInfo) {}
    fun field(info: FieldInfo) {}
}

internal object NoOpTypeVisitor : TypeVisitor

class ModuleInfo(val name: String)

sealed class TypeInfo(val name: String, val superClass: String?, val interfaces: List<String>)

class ClassInfo(name: String, superClass: String?, interfaces: List<String>) : TypeInfo(name, superClass, interfaces)

class InterfaceInfo(name: String, superClass: String?, interfaces: List<String>) : TypeInfo(name, superClass, interfaces)

class EnumInfo(name: String, superClass: String?, interfaces: List<String>) : TypeInfo(name, superClass, interfaces)

class AnnotationInfo(name: String, superClass: String?, interfaces: List<String>) : TypeInfo(name, superClass, interfaces)

class MethodInfo(val name: String, val parameterTypes: List<String>, val returnType: String)

class FieldInfo(val name: String, val fieldType: String)
