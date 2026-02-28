package sample

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.one
import net.rubygrapefruit.parse.binary.parse
import net.rubygrapefruit.parse.combinators.*

class Parser {
    fun parse(file: RegularFile): List<Image> {
        val u16le = sequence(one(), one()) { b1, b2 -> b2.toUByte().toUInt().rotateLeft(8).or(b1.toUByte().toUInt()) }
        val u32le = sequence(u16le, u16le) { w1, w2 -> w2.rotateLeft(16).or(w1) }

        val u16be = sequence(one(), one()) { b1, b2 -> b1.toUByte().toUInt().rotateLeft(8).or(b2.toUByte().toUInt()) }
        val u32be = sequence(u16be, u16be) { w1, w2 -> w1.rotateLeft(16).or(w2) }

        val magic64le = literal(byteArrayOf(0xcf.toByte(), 0xfa.toByte(), 0xed.toByte(), 0xfe.toByte()))

        val cpule = sequence(u32le, u32le) { cpu, subtype -> cpu(cpu, subtype) }
        val header64le = sequence(magic64le, cpule)
        val image64le = map(header64le) { cpu -> listOf(MachOImage(cpu)) }
        val file64le = sequence(image64le, discard(zeroOrMore(one())))

        val magicUniversal = literal(byteArrayOf(0xca.toByte(), 0xfe.toByte(), 0xba.toByte(), 0xbe.toByte()))

        val cpube = sequence(u32be, u32be) { cpu, subtype -> cpu(cpu, subtype) }
        val binaryHeader = sequence(cpube, repeat(3, u32be)) { cpu, _ -> MachOImage(cpu) }

        val binaryHeaders = decide(u32be) { repeat(it.toInt(), binaryHeader) }
        val executables = sequence(magicUniversal, binaryHeaders)
        val fileUniversal = sequence(executables, discard(zeroOrMore(one())))

        val parser = oneOf(file64le, fileUniversal)

        return parser.parse(file.readBytes()).get()
    }

    private fun cpu(cpuType: UInt, subType: UInt): CPU {
        return when (cpuType) {
            0x01000007.toUInt() -> CPU("64-bit x86")
            0x0100000C.toUInt() -> CPU("64-bit ARM")
            else -> throw IllegalArgumentException("Unknown CPU type: ${cpuType.toHexString(HexFormat.UpperCase)}")
        }
    }
}