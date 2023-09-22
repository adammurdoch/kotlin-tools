plugins {
    id("net.rubygrapefruit.bootstrap.gradle-plugin")
}

group = "net.rubygrapefruit.plugins"

dependencies {
    implementation("net.rubygrapefruit.libs:bytecode:1.0")
    implementation("net.rubygrapefruit.libs:machine-info:1.0")
}

gradlePlugin {
    plugins {
        create("settings") {
            id = "net.rubygrapefruit.kotlin-base"
            implementationClass = "net.rubygrapefruit.app.plugins.KotlinBasePlugin"
        }
        create("included-builds") {
            id = "net.rubygrapefruit.included-build"
            implementationClass = "net.rubygrapefruit.app.plugins.IncludedBuildLifecyclePlugin"
        }
        create("plugin") {
            id = "net.rubygrapefruit.gradle-plugin"
            implementationClass = "net.rubygrapefruit.app.plugins.GradlePluginPlugin"
        }
        create("mpp-lib") {
            id = "net.rubygrapefruit.mpp.lib"
            implementationClass = "net.rubygrapefruit.app.plugins.MppLibraryPlugin"
        }
        create("native-lib") {
            id = "net.rubygrapefruit.native.lib"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeLibraryPlugin"
        }
        create("native-cli-app") {
            id = "net.rubygrapefruit.native.cli-app"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeCliApplicationPlugin"
        }
        create("jvm-lib") {
            id = "net.rubygrapefruit.jvm.lib"
            implementationClass = "net.rubygrapefruit.app.plugins.JvmLibraryPlugin"
        }
        create("jvm-cli-app") {
            id = "net.rubygrapefruit.jvm.cli-app"
            implementationClass = "net.rubygrapefruit.app.plugins.JvmCliApplicationPlugin"
        }
        create("embedded-jvm") {
            id = "net.rubygrapefruit.jvm.embedded-jvm"
            implementationClass = "net.rubygrapefruit.app.plugins.EmbeddedJvmLauncherPlugin"
        }
    }
}
