plugins {
    samples.multiplatform()
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(samples.coordinates())
            }
        }
    }
}
