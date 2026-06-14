package net.rubygrapefruit.parse

internal interface InputPredicate<in IN> {
    fun match(input: IN, index: Int): Boolean
}