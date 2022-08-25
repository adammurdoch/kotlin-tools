plugins {
    id("java-gradle-plugin")
    alias(versions.plugins.kotlinJvmPlugin)
}

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
            implementationClass = "net.rubygrapefruit.app.plugins.NativeCLIApplicationPlugin"
        }
    }
}
