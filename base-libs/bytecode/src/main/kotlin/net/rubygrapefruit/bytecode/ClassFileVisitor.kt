package net.rubygrapefruit.bytecode

interface ClassFileVisitor {
    /**
     * Visits a module.
     */
    fun module(module: ModuleInfo) {}

    /**
     * Visits a type. Can return a `TypeVisitor` to receive information about the methods and fields of the type.
     */
    fun type(type: TypeInfo): TypeVisitor? = null
}

interface TypeVisitor {
    fun method(method: MethodInfo) {}
    fun field(field: FieldInfo) {}
}

internal object NoOpTypeVisitor : TypeVisitor

class ModuleInfo(val name: String)

sealed class TypeInfo constructor(val name: String, val superClass: String?, val interfaces: List<String>, private val accessFlags: UInt) {
    val isPublic: Boolean
        get() = AccessFlag.Public.containedIn(accessFlags)
    val isFinal: Boolean
        get() = AccessFlag.Final.containedIn(accessFlags)
    val isAbstract: Boolean
        get() = AccessFlag.Abstract.containedIn(accessFlags)
    val isSynthetic: Boolean
        get() = AccessFlag.Synthetic.containedIn(accessFlags)
}

class ClassInfo internal constructor(name: String, superClass: String?, interfaces: List<String>, accessFlags: UInt) : TypeInfo(name, superClass, interfaces, accessFlags)

class InterfaceInfo internal constructor(name: String, superClass: String?, interfaces: List<String>, accessFlags: UInt) : TypeInfo(name, superClass, interfaces, accessFlags)

class EnumInfo internal constructor(name: String, superClass: String?, interfaces: List<String>, accessFlags: UInt) : TypeInfo(name, superClass, interfaces, accessFlags)

class AnnotationInfo internal constructor(name: String, superClass: String?, interfaces: List<String>, accessFlags: UInt) : TypeInfo(name, superClass, interfaces, accessFlags)

class MethodInfo internal constructor(val name: String, val parameterTypes: List<String>, val returnType: String, private val accessFlags: UInt) {
    val isPublic: Boolean
        get() = AccessFlag.Public.containedIn(accessFlags)
    val isPrivate: Boolean
        get() = AccessFlag.Private.containedIn(accessFlags)
    val isProtected: Boolean
        get() = AccessFlag.Protected.containedIn(accessFlags)
    val isStatic: Boolean
        get() = AccessFlag.Static.containedIn(accessFlags)
    val isFinal: Boolean
        get() = AccessFlag.Final.containedIn(accessFlags)
    val isAbstract: Boolean
        get() = AccessFlag.Abstract.containedIn(accessFlags)
    val isSynchronized: Boolean
        get() = AccessFlag.Synchronized.containedIn(accessFlags)
    val isBridge: Boolean
        get() = AccessFlag.Bridge.containedIn(accessFlags)
    val isVarargs: Boolean
        get() = AccessFlag.Varargs.containedIn(accessFlags)
    val isNative: Boolean
        get() = AccessFlag.Native.containedIn(accessFlags)
    val isSynthetic: Boolean
        get() = AccessFlag.Synthetic.containedIn(accessFlags)
}

class FieldInfo internal constructor(val name: String, val fieldType: String, private val accessFlags: UInt) {
    val isPublic: Boolean
        get() = AccessFlag.Public.containedIn(accessFlags)
    val isPrivate: Boolean
        get() = AccessFlag.Private.containedIn(accessFlags)
    val isProtected: Boolean
        get() = AccessFlag.Protected.containedIn(accessFlags)
    val isStatic: Boolean
        get() = AccessFlag.Static.containedIn(accessFlags)
    val isFinal: Boolean
        get() = AccessFlag.Final.containedIn(accessFlags)
    val isVolatile: Boolean
        get() = AccessFlag.Volatile.containedIn(accessFlags)
    val isTransient: Boolean
        get() = AccessFlag.Transient.containedIn(accessFlags)
    val isSynthetic: Boolean
        get() = AccessFlag.Synthetic.containedIn(accessFlags)
}
