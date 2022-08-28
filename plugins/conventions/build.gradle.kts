plugins {
    id("java-gradle-plugin")
    alias(versions.plugins.kotlinJvmPlugin)
}

group="net.rubygrapefruit.plugins"

repositories {
    mavenCentral()
}

dependencies {
    implementation(versions.kotlinJvmPlugin)
}

gradlePlugin {
    plugins {
        create("native-cli-app") {
            id = "net.rubygrapefruit.native-cli-app"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeCliApplicationPlugin"
        }
        create("jvm-cli-app") {
            id = "net.rubygrapefruit.jvm-cli-app"
            implementationClass = "net.rubygrapefruit.app.plugins.JvmCliApplicationPlugin"
        }
        create("embedded-jvm") {
            id = "net.rubygrapefruit.embedded-jvm"
            implementationClass = "net.rubygrapefruit.app.plugins.EmbeddedJvmLauncherPlugin"
        }
    }
}
