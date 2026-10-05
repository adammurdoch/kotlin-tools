package net.rubygrapefruit.app.ui.launcher

import net.rubygrapefruit.app.launcher.propagateMainFailure

fun runMain(main: () -> Unit) {
    // See note in macOS runMain()
    propagateMainFailure()
    main()
}