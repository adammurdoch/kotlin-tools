package net.rubygrapefruit.parse

/**
 * A parser that takes input of type [IN] and produces a result of type [OUT].
 *
 * A parser that does not produce a result is represented using an [OUT] of `Unit`.
 */
interface Parser<in IN, out OUT> {
}