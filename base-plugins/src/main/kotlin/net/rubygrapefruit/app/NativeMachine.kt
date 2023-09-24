package net.rubygrapefruit.app

enum class NativeMachine(
    val kotlinTarget: String
) {
    MacOSX64("macosX64"),
    MacOSArm64("macosArm64"),
    WindowsX64("mingwX64"),
    LinuxX64("linuxX64");
}