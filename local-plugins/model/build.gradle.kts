plugins {
    id("net.rubygrapefruit.stage2.jvm.lib")
    id("net.rubygrapefruit.stage2.serialization")
}

dependencies {
    implementation(gradleApi())
    testImplementation(buildConstants.kotlin.test.coordinates)
}
