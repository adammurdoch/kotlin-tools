plugins {
    id("net.rubygrapefruit.kmp.base-lib")
    id("net.rubygrapefruit.bootstrap.release")
}

group = versions.libs.group

library {
    jvm {
        targetJvmVersion = versions.plugins.java
        module.name = "net.rubygrapefruit.stream_io"
    }
    nativeDesktop()
    common {
        api(versions.io.coordinates)
        implementation(versions.libs.coordinates("basics"))
    }
    test {
        implementation(versions.test.coordinates)
    }
}

component {
    description = "A Kotlin multiplatform library for binary and text IO."
    nextVersion = "0.0.2-milestone-1"
}
