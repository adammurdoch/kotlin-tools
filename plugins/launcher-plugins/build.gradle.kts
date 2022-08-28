plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm")
}

group = "net.rubygrapefruit.plugins"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":conventions"))
    implementation(project(":download"))
}

gradlePlugin {
    plugins {
        create("native-binary") {
            id = "net.rubygrapefruit.native-binary"
            implementationClass = "net.rubygrapefruit.app.plugins.NativeBinaryJvmLauncherPlugin"
        }
    }
}
