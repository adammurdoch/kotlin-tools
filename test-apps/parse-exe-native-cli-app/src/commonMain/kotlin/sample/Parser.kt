package sample

import net.rubygrapefruit.file.RegularFile
import net.rubygrapefruit.parse.binary.literal
import net.rubygrapefruit.parse.binary.one
import net.rubygrapefruit.parse.binary.parse
import net.rubygrapefruit.parse.combinators.*

class Parser {
    fun parse(file: RegularFile): ExeDetails {
        val u16le = sequence(one(), one()) { b1, b2 -> b2.toUByte().toUInt().rotateLeft(8).or(b1.toUByte().toUInt()) }
        val u32le = sequence(u16le, u16le) { w1, w2 -> w2.rotateLeft(16).or(w1) }

        val u16be = sequence(one(), one()) { b1, b2 -> b1.toUByte().toUInt().rotateLeft(8).or(b2.toUByte().toUInt()) }
        val u32be = sequence(u16be, u16be) { w1, w2 -> w1.rotateLeft(16).or(w2) }

        val magic64le = literal(byteArrayOf(0xcf.toByte(), 0xfa.toByte(), 0xed.toByte(), 0xfe.toByte()))

        val cpu = sequence(u32le, u32le) { cpu, subtype -> Pair(cpu, subtype) }
        val header64le = sequence(magic64le, cpu)
        val file64le = sequence(header64le, discard(zeroOrMore(one())))

        val magicUniversal = literal(byteArrayOf(0xca.toByte(), 0xfe.toByte(), 0xba.toByte(), 0xbe.toByte()))
        val headerUniversal = sequence(magicUniversal, u32be)
        val fileUniversal = sequence(headerUniversal, discard(zeroOrMore(one())))

        val parser = oneOf(
            map(file64le) {
                println("-> 64 bit little endian")
                println("-> CPU: ${it.first.toHexString(HexFormat.UpperCase)}")
                println("-> CPU subtype: ${it.second.toHexString(HexFormat.UpperCase)}")
                ExeDetails()
            },
            map(fileUniversal) {
                println("-> Universal")
                println("-> Binaries: ${it.toHexString(HexFormat.UpperCase)}")
                ExeDetails()
            }
        )

        return parser.parse(file.readBytes()).get()
    }
}