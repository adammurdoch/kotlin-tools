pluginManagement {
    includeBuild("../stage2")
    includeBuild("../local-plugins")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
    id("net.rubygrapefruit.bootstrap.test-apps")
}

samples {
    jvmLib("jvm-lib") {
        derive("jvm-lib-customized")
    }

    jvmLib("jvm-lib-generated-source")

    jvmLib("docs-test")

    kmpLib("kmp-lib") {
        derive("kmp-lib-customized")
    }

    kmpLib("kmp-lib-render") {
        derive("kmp-lib-render-customized")
    }

    kmpLib("kmp-lib-generated-source")

    kmpLib("native-lib")

    kmpLib("native-lib-generated-source")

    jvmCliApp("jvm-cli-app-min") {
        cliArgs("hello", "world")
        expectedOutput("args: hello, world")

        deriveNative("native-cli-app-min")
    }

    jvmCliApp("jvm-cli-app") {
        cliArgs("1", "+", "2")
        expectedOutput("Expression: (1) + (2)")
        derive("jvm-cli-app-customized") {
            launcher("app")
        }
        derive("jvm-cli-app-embedded") {
            embeddedJvm()
        }
        derive("jvm-cli-app-embedded-customized") {
            embeddedJvm()
            launcher("app")
        }
        derive("jvm-cli-app-native-binary") {
            nativeBinaries()
        }
        derive("jvm-cli-app-native-binary-customized") {
            nativeBinaries()
            launcher("app")
        }
        derive("jvm-cli-app-java11") {
            requiresJvm(11)
        }
        derive("jvm-cli-app-java25") {
            requiresJvm(24)
        }
        deriveNative("native-cli-app") {
            derive("native-cli-app-customized") {
                launcher("app")
            }
        }
    }

    jvmCliApp("jvm-cli-app-full") {
        cliArgs("list")
        deriveNative("native-cli-app-full")
    }
    jvmCliApp("store-jvm-cli-app") {
        cliArgs("content", "build/test")
        deriveNative("store-native-cli-app")
    }
    jvmCliApp("jvm-cli-app-generated-source") {
        expectedOutput("Generated app class")
    }

    jvmCliApp("cli-args-parameters") { cliArgs("--help") }
    jvmCliApp("cli-args-options") { cliArgs("--help") }
    jvmCliApp("cli-args-actions") { cliArgs("--help") }

    nativeCliApp("native-cli-app-generated-source") {
        expectedOutput("Generated common app class")
    }

    jvmUiApp("jvm-ui-app") {
        derive("jvm-ui-app-customized") {
            launcher("App")
        }
    }

    nativeUiApp("native-ui-app") {
        derive("native-ui-app-customized") {
            launcher("App")
        }
    }
}
