package net.rubygrapefruit.process

import net.rubygrapefruit.machine.info.Machine

actual fun isWindows(): Boolean {
    return Machine.thisMachine.isWindows
}