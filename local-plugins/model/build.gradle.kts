plugins {
    id("net.rubygrapefruit.convention.build-jvm-lib")
    id("net.rubygrapefruit.stage2.serialization")
}

library {
    targetJvmVersion = buildConstants.plugins.jvm.version
}

dependencies {
    implementation(gradleApi())
    testImplementation(buildConstants.kotlin.test.coordinates)
}
