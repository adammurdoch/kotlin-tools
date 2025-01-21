plugins {
    kotlin("multiplatform") version "2.1.0"
}

repositories {
    gradlePluginPortal()
}

kotlin {
    targets {
        jvm()
        macosArm64 {
            binaries {
                executable()
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("net.rubygrapefruit.libs:basics:0.0.1-milestone-1")
            }
        }
    }
}