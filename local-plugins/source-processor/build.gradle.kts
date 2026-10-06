plugins {
    id("net.rubygrapefruit.convention.base-jvm-lib")
}

dependencies {
    implementation(versions.ksp.coordinates)
    implementation(versions.serialization.json.coordinates)
    implementation(project(":app-metadata"))
}