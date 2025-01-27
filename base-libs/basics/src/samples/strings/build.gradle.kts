plugins {
    samples.multiplatform()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("net.rubygrapefruit:basics:0.0.1")
            }
        }
    }
}
