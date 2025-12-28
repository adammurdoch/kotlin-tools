pluginManagement {
    includeBuild("../stage2")
    includeBuild("../local-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
    id("net.rubygrapefruit.stage2.test-apps")
}

samples {
    val jvmLib = jvmLib("jvm-lib")
    jvmLib.derive("jvm-lib-customized")

    jvmLib("jvm-lib-generated-source")

    jvmLib("docs-test")

    val kmpLib = kmpLib("kmp-lib")
    kmpLib.derive("kmp-lib-customized")

    val kmpLibRender = kmpLib("kmp-lib-render")
    kmpLibRender.derive("kmp-lib-render-customized")

    kmpLib("kmp-lib-generated-source")

    kmpLib("native-lib")

    kmpLib("native-lib-generated-source")

    val jvmCliMinApp = jvmCliApp("jvm-cli-app-min") {
        cliArgs("hello", "world")
        expectedOutput("args: hello, world")
    }

    val jvmCliApp = jvmCliApp("jvm-cli-app") {
        cliArgs("1", "+", "2")
        expectedOutput("Expression: (1) + (2)")
    }
    jvmCliApp.derive("jvm-cli-app-customized") {
        launcher("app")
    }
    jvmCliApp.derive("jvm-cli-app-embedded") {
        embeddedJvm()
    }
    jvmCliApp.derive("jvm-cli-app-embedded-customized") {
        embeddedJvm()
        launcher("app")
    }
    jvmCliApp.derive("jvm-cli-app-native-binary") {
        nativeBinaries()
    }
    jvmCliApp.derive("jvm-cli-app-native-binary-customized") {
        nativeBinaries()
        launcher("app")
    }
    jvmCliApp.derive("jvm-cli-app-java11") {
        requiresJvm(11)
    }
    jvmCliApp.derive("jvm-cli-app-java25") {
        requiresJvm(24)
    }

    val jvmCliFullApp = jvmCliApp("jvm-cli-app-full") {
        cliArgs("list")
    }
    val jvmCliStoreApp = jvmCliApp("store-jvm-cli-app") {
        cliArgs("content", "build/test")
    }
    jvmCliApp("jvm-cli-app-generated-source") {
        expectedOutput("Generated app class")
    }

    jvmCliApp("cli-args-parameters") { cliArgs("--help") }
    jvmCliApp("cli-args-options") { cliArgs("--help") }
    jvmCliApp("cli-args-actions") { cliArgs("--help") }

    jvmCliMinApp.deriveNative("native-cli-app-min")

    val nativeCliApp = jvmCliApp.deriveNative("native-cli-app")
    nativeCliApp.derive("native-cli-app-customized") {
        launcher("app")
    }

    jvmCliFullApp.deriveNative("native-cli-app-full")
    jvmCliStoreApp.deriveNative("store-native-cli-app")

    nativeCliApp("native-cli-app-generated-source") {
        expectedOutput("Generated common app class")
    }

    val jvmUiApp = jvmUiApp("jvm-ui-app")
    jvmUiApp.derive("jvm-ui-app-customized")

    val nativeUiApp = nativeUiApp("native-ui-app")
    nativeUiApp.derive("native-ui-app-customized")
}
