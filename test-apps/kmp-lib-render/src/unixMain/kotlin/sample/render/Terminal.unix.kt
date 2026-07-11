package sample.render

import platform.posix.isatty

actual fun isAnsiTerminal(): Boolean {
    return isatty(1) != 0
}
