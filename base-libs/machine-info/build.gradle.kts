plugins {
    id("net.rubygrapefruit.bootstrap.jvm.lib")
}

group = "net.rubygrapefruit.libs"

dependencies {
    implementation(project(":cpu-info"))
}