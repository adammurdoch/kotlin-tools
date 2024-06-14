package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

open class Action {
    private val options = mutableListOf<DefaultFlag>()

    /**
     * Defines a boolean flag with the given name.
     */
    fun flag(name: String, default: Boolean = false): Flag {
        val flag = DefaultFlag(name, default)
        options.add(flag)
        return flag
    }

    /**
     * Configures this object from the given arguments.
     */
    @Throws(ArgParseException::class)
    fun parse(args: List<String>) {
        for (arg in args) {
            var matched = false
            for (option in options) {
                if (option.accept(arg)) {
                    matched = true
                    break
                }
            }
            if (!matched) {
                throw ArgParseException("Unknown option: $arg")
            }
        }
    }

    sealed interface Flag {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean
    }

    internal class DefaultFlag(name: String, default: Boolean) : Flag {
        private val enableFlag = "--$name"
        private val disableFlag = "--no-$name"
        private var value: Boolean = default

        override operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return value
        }

        fun accept(arg: String): Boolean {
            return if (arg == enableFlag) {
                value = true
                true
            } else {
                if (arg == disableFlag) {
                    value = false
                    true
                } else {
                    false
                }
            }
        }
    }
}