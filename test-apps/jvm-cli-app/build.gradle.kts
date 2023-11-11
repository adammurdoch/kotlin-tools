plugins {
    id("net.rubygrapefruit.jvm.cli-app")
}

application {
    mainClass = "sample.MainKt"
}

dependencies {
    implementation(project(":kmp-lib"))
    implementation(project(":jvm-lib"))
}
