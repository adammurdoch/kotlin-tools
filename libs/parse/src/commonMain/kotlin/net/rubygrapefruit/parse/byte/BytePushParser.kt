package net.rubygrapefruit.parse.byte

import net.rubygrapefruit.parse.PushParser

interface BytePushParser<OUT> : PushParser<OUT> {
    fun parse(bytes: ByteArray)
}