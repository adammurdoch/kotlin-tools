plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    mainClass = "sample.MainKt"
}

dependencies {
    implementation(project(":mpp-lib"))
    implementation(project(":jvm-lib"))
}
