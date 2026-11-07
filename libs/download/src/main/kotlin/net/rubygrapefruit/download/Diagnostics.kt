package net.rubygrapefruit.download

import java.net.URI

interface Diagnostics {
    fun downloading(url: URI)

    object Logging : Diagnostics {
        override fun downloading(url: URI) {
            println("Downloading $url")
        }
    }

    object Silent : Diagnostics {
        override fun downloading(url: URI) {
        }
    }
}