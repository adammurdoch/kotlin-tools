Stage 2
-------

Plugins implemented using Kotlin that provide support for writing and releasing libraries and Gradle plugins implemented using Kotlin.

Gradle plugin plugin
    - Produces releasable plugins
    - Extends stage 1 plugin
Jvm library plugin
    - Produces releasable JVM libraries
    - Adds a DSL
KMP library plugin
    - Produces releasable KMP libraries that target JVM, macOS, Linux, Windows and the browser
JNI library plugin
    - Produces releasable Java + native libraries
Serialization plugin
    - Also adds JSON as an implementation dependency
Settings plugin
    - Extends stage 1 plugin
Included build plugin
    - Adds lifecycle tasks
Stage 3 DSL plugin
    - Provides a DSL used by stage 3

These plugins are not released and are intended to be used only by this build.
