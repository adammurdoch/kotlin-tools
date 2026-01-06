package net.rubygrapefruit.parse.char

import net.rubygrapefruit.parse.PushParser

interface CharPushParser<OUT> : PushParser<OUT> {
    fun parse(chars: CharArray)
}