plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm")
}

group = "net.rubygrapefruit.plugins"

repositories {
    mavenCentral()
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
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
        create("native-cli-app") {
            id = "net.rubygrapefruit.native.cli-app"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeCliApplicationPlugin"
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
