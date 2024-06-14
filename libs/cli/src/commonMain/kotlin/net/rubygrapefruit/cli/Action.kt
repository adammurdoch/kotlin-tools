package net.rubygrapefruit.cli

import kotlin.reflect.KProperty

open class Action {
    private val options = mutableListOf<DefaultFlag>()

    fun flag(name: String): Flag {
        val flag = DefaultFlag(name)
        options.add(flag)
        return flag
    }

    fun parse(args: List<String>) {
        for (arg in args) {
            for (option in options) {
                if (option.accept(arg)) {
                    break
                }
            }
        }
    }

    sealed interface Flag {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean
    }

    internal class DefaultFlag(name: String) : Flag {
        private val enableFlag = "--$name"
        private val disableFlag = "--no-$name"
        private var value = false

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