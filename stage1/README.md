Stage 1
-------

Plugins implemented using Java that provide basic support for writing non-released Gradle plugins implemented using Kotlin.

- JVM base plugin
  - Makes build constants available to plugin implementation via extension and API.
  - Sets up target Kotlin and JVM versions.
- Gradle plugin plugin
  - Support for implementing Gradle plugins that are:
    - Implemented using Java
    - Not released
  - Same as JVM base plugin above 
  - Provides a DSL for plugin declaration.
- Settings plugin
  - Sets up Java toolchains
  - Adds Kotlin and serialization plugins to root project classpath 
- Included build plugin 
  - Adds lifecycle tasks

These plugins are not released and are intended to be used only by this build.
