Stage 3
-------

Builds local copies of release libraries and Gradle plugins that are then used to build the released binaries.

The DSL in `settings.gradle.kts` defines which production projects to downgrade into stage 3.
Run `./gradlew stage3:generate` when the set of projects changes or when the build script for these projects changes.
