Stage 3
-------

Builds local copies of release libraries and Gradle plugins that are then used to build the released binaries.

The DSL in `settings.gradle.kts` defines which production projects to downgrade into stage 3.
Run `./gradlew stage3:generate` when the set of projects changes or when the build script for these projects changes.

Provides some convention plugins for this build:

- net.rubygrapefruit.convention.base-jvm-lib - A very general purpose JVM library. Targets the 'base' JVM defined in `versions.toml`
- net.rubygrapefruit.convention.build-jvm-lib - A JVM library used by plugins. Targets the 'plugins' JVM defined in `versions.toml`
- net.rubygrapefruit.convention.build-jvm-lib - A general JVM library. Targets the 'libs' JVM defined in `versions.toml`
- net.rubygrapefruit.convention.base-kmp-lib - A very general purpose KMP library.

Convention plugins also apply build constants and a group
