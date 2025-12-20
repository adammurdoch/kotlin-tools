Stage 1
-------

Plugins implemented in Java that provide basic support for Gradle plugins implemented in Kotlin.

- Gradle plugin plugin
  - Provides a DSL for plugin declaration.
  - Makes build constants available to plugin implementation via extension and API.
  - Sets up target Kotlin and JVM versions.
- Settings plugin
  - Sets up Java toolchains
  - Adds Kotlin and serialization plugins to root project classpath 
  - Adds lifecycle tasks
