plugins {
    id("net.rubygrapefruit.stage2.jni.lib")
}

library {
    targetJavaVersion = buildConstants.plugins.jvm.version
}
