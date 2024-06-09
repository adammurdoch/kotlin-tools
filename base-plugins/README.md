# Gradle Plugins

A collection of Gradle plugins for building applications and libraries using Kotlin.

These plugins all target Kotlin 2.0.0 and Java 17 (for Kotlin/JVM apps).
The plugins require Gradle 7.5.1 or later and Java 11 or later.

## `net.rubygrapefruit.kotlin-base`

A settings plugin that must be applied to the settings of any build that uses these plugins.

## `net.rubygrapefruit.native.desktop-lib`

Builds a library implemented in Kotlin multiplatform that targets native desktop platforms.

- Adds Linux and Windows x64 targets and macOS x64 and arm64 targets.
- Adds `unixMain` source sets for Kotlin code that is shared by macOS and Linux targets.
- Adds a `library { }` block.

## `net.rubygrapefruit.native.cli-app`

Builds a command-line application implemented in Kotlin multiplatform that targets native desktop platforms.

- Adds Linux and Windows x64 targets and macOS x64 and arm64 targets.
- Adds `unixMain` and `unixTest` source sets for Kotlin code that is shared by macOS and Linux targets.
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.native.ui-app`

Builds a UI application implemented in Kotlin multiplatform that targets native desktop platforms.

## `net.rubygrapefruit.native.base-cli-app`

Builds a CLI app implemented in Kotlin multiplatform. Does not define any targets, these have to be explicitly 
defined.

## `net.rubygrapefruit.kmp.base-lib`

Builds a library implemented in Kotlin multiplatform. Does not define any targets, these have to be explicitly 
defined.

## `net.rubygrapefruit.kmp.lib`

Builds a library implemented in Kotlin multiplatform that targets the JVM, browser and native desktop platforms.

- Add JVM target.
- Add browser target.
- Adds Linux and Windows x64 targets and macOS x64 and arm64 targets.
- Adds `library { }` block, see below for the available settings.
- Generates a `module-info` for the JVM target.

## `net.rubygrapefruit.jvm.lib`

Builds a library implemented in Kotlin/JVM.

- Adds `library { }` block, see below for the available settings.
- Generates a `module-info`.

## `net.rubygrapefruit.jvm.cli-app`

Builds a command-line application implemented in Kotlin/JVM.
Expects that the application uses the JVM module system.

- The distribution contains the jars for the application and a launcher script. 
  This script requires that a compatible JVM can be found on the machine's `$PATH`. 
- Adds `application { }` block, see below for the available settings.
- Automatically determines the application main class.
- Generates a `module-info`.

## `net.rubygrapefruit.jvm.embedded-jvm`

Can be applied with `net.rubygrapefruit.jvm.cli-app` to include a copy of the JVM in the application distribution.
The launcher script uses this embedded JVM. Uses `jlink` to create the JVM image.

## `net.rubygrapefruit.jvm.native-binary`

Can be applied with `net.rubygrapefruit.jvm.cli-app` to create a native binary to use in the application distribution,
instead of using a launcher script. Uses `graalvm` to create the native binary.

## `net.rubygrapefruit.jvm.ui-app`

Builds a desktop UI application implemented in Kotlin/JVM and that presents a Swing or JavaFX UI.
Expects that the application uses the JVM module system.

- Creates a debug (unsigned) macOS application bundle and a release (signed and notarized) application bundle
- Includes an embedded JVM in the application bundle
- Provides an application icon, default location is `src/main/Icon1024.png`
- Use `dist` task to build the debug application bundle
- Use `releaseDist` to build the release application bundle
- Adds `application { }` block, see below for the available settings.

## `net.rubygrapefruit.gradle-plugin`

A convention plugin for implementing Gradle plugins in Kotlin/JVM.

- Adds dependencies and repository definitions so that a fixed version of Kotlin (currently 2.0.0) is used to compile the plugin.
- Targets Java 11.
- Adds `versions` extension
- Adds a dependency on these plugins, so their API can be used in other plugins.

## Common application settings for all targets

- Adds `application { }` block
  - `application.appName` - the base name for the application, used for various default file names.
  - `application.distribution.imageDirectory` - the directory to create the distribution image in.
- Adds a `dist` task to create a distribution image containing the app ready for installation.
  By default, this is created in `build/dist-image`

## JVM application settings

- Adds `application { }` block
  - `application.mainClass` - defaults to main class determined by inspecting the bytecode
  - `application.module.name` - defaults to the application name
  - `application.targetJavaVersion` - defaults to 17
  - `application.dependencies { }` - production dependencies
  - `application.test { }` - test dependencies

## JVM library settings

- Adds `library { }` block
  - `library.module.name` - defaults to the project name 
  - `library.module.exports` 
  - `library.module.requires` - calculated from compile (implementation) dependencies 
  - `library.module.requiresTransitive` - calculated from the API dependencies
  - `library.targetJavaVersion` - defaults to 17
  - `library.dependencies { }` - production dependencies
  - `library.test { }` - test dependencies

## Native application settings

- Adds `application { }` block
  - `application.common { }` - production dependencies common to all targets
  - `application.test { }` - test dependencies common to all targets

## Multi-platform library settings

- Adds `library { }` block
  - `library.jvm { }` - JVM target settings (same as JVM library above) 
  - `library.common { }` - production dependencies common to all targets
  - `library.test { }` - test dependencies common to all targets

See [`../test-apps`](../test-apps/) for some samples.
