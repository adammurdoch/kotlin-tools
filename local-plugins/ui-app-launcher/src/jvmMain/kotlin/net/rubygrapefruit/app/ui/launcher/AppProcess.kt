package net.rubygrapefruit.app.ui.launcher

import net.rubygrapefruit.app.launcher.finishMain
import net.rubygrapefruit.app.launcher.propagateMainFailure

fun runMain(main: () -> Unit): Nothing {
    try {
        propagateMainFailure()
        main()
        finishMain(null)
    } catch (t: Throwable) {
        finishMain(t)
    }
}