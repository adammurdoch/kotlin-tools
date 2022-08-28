plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm")
}

group = "net.rubygrapefruit.plugins"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
}

gradlePlugin {
    plugins {
        create("settings") {
            id = "net.rubygrapefruit.kotlin-apps"
            implementationClass = "net.rubygrapefruit.app.plugins.KotlinAppsPlugin"
        }
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
