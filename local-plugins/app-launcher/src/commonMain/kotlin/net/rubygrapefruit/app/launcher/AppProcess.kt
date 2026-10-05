package net.rubygrapefruit.app.launcher

fun finishMain(failure: Throwable?): Nothing {
    if (failure != null) {
        failure.printStackTrace()
        exit(1)
    } else {
        exit(0)
    }
}

expect fun exit(status: Int): Nothing
