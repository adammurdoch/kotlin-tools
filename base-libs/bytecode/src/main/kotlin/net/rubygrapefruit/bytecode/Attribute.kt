package net.rubygrapefruit.bytecode

internal sealed class Attribute {
    abstract fun Encoder.writeTo()
}

internal class ModuleAttribute(
    private val attributeName: StringEntry,
    private val moduleInfo: ModuleInfoEntry,
    private val javaBaseModuleInfo: ModuleInfoEntry,
    private val javaVersion: StringEntry,
    private val requires: List<ModuleInfoEntry>,
    private val requiresTransitive: List<ModuleInfoEntry>,
    private val exports: List<PackageInfoEntry>
) : Attribute() {
    override fun Encoder.writeTo() {
        u2(attributeName.index.toUInt())
        val encoded = writing {
            u2(moduleInfo.index.toUInt())
            // access flags
            u2(0u)
            // module version
            u2(0u)
            // requires count
            u2((requires.size + requiresTransitive.size + 1).toUInt())
            u2(javaBaseModuleInfo.index.toUInt())
            u2(0x8000u)
            u2(javaVersion.index.toUInt())
            for (module in requires) {
                u2(module.index.toUInt())
                u2(0x8000u)
                u2(0u)
            }
            for (module in requiresTransitive) {
                u2(module.index.toUInt())
                u2(0x8020u)
                u2(0u)
            }
            // exports count
            u2(exports.size.toUInt())
            for (export in exports) {
                u2(export.index.toUInt())
                u2(0x8000u)
                u2(0u)
            }
            // opens count
            u2(0u)
            // uses count
            u2(0u)
            // provides count
            u2(0u)
        }
        u4(encoded.size.toUInt())
        bytes(encoded)
    }
}
