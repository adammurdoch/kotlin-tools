plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

group = "net.rubygrapefruit.libs"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}
