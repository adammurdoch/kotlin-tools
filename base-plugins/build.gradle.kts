import net.rubygrapefruit.plugins.app.Versions

plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
}

group = Versions.pluginsGroup

dependencies {
    implementation("net.rubygrapefruit.libs:bytecode:1.0")
    implementation("net.rubygrapefruit.libs:machine-info:1.0")
}

gradlePlugin {
    plugins {
        create("settings") {
            id = "net.rubygrapefruit.kotlin-base"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.KotlinBasePlugin"
        }
        create("included-builds") {
            id = "net.rubygrapefruit.included-build"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.IncludedBuildLifecyclePlugin"
        }
        create("plugin") {
            id = "net.rubygrapefruit.gradle-plugin"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.GradlePluginPlugin"
        }
        create("kmp-base-lib") {
            id = "net.rubygrapefruit.kmp.base-lib"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.KmpBaseLibraryPlugin"
        }
        create("kmp-lib") {
            id = "net.rubygrapefruit.kmp.lib"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.KmpLibraryPlugin"
        }
        create("native-lib") {
            id = "net.rubygrapefruit.native.desktop-lib"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.NativeDesktopLibraryPlugin"
        }
        create("native-base-cli-app") {
            id = "net.rubygrapefruit.native.base-cli-app"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.NativeBaseCliApplicationPlugin"
        }
        create("native-cli-app") {
            id = "net.rubygrapefruit.native.cli-app"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.NativeCliApplicationPlugin"
        }
        create("jvm-lib") {
            id = "net.rubygrapefruit.jvm.lib"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.JvmLibraryPlugin"
        }
        create("jvm-cli-app") {
            id = "net.rubygrapefruit.jvm.cli-app"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.JvmCliApplicationPlugin"
        }
        create("embedded-jvm") {
            id = "net.rubygrapefruit.jvm.embedded-jvm"
            implementationClass = "net.rubygrapefruit.plugins.app.internal.plugins.EmbeddedJvmLauncherPlugin"
        }
    }
}
