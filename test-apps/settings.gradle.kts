pluginManagement {
    includeBuild("../stage2")
    includeBuild("../local-plugins")
    includeBuild("../libs")
}
plugins {
    id("net.rubygrapefruit.kotlin-base")
    id("net.rubygrapefruit.stage2.included-build")
    id("net.rubygrapefruit.bootstrap.test-apps")
}

samples {
    jvmLib("jvm-lib") {
        derive("jvm-lib-customized") {
            requiresJvm(11)
        }
    }

    jvmLib("jvm-lib-generated-source") {
        noSourceDirs()
    }

    jvmLib("docs-test")

    kmpLib("kmp-lib") {
        derive("kmp-lib-customized") {
            requiresJvm(11)
        }
    }

    kmpLib("kmp-lib-render") {
        derive("kmp-lib-render-customized") {
            requiresJvm(11)
        }
    }

    kmpLib("kmp-lib-generated-source") {
        noSourceDirs()
    }

    kmpLib("native-lib") {
        noJvm()
    }

    kmpLib("parse-kmp-lib")

    jvmCliApp("jvm-cli-app-min") {
        cliArgs("hello", "world")
        expectedOutput("args: hello, world")

        deriveNative("native-cli-app-min")
    }

    jvmCliApp("jvm-cli-app") {
        cliArgs("1", "+", "2")
        expectedOutput("1 + 2 = 3")
        derive("jvm-cli-app-customized") {
            requiresJvm(21)
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
        derive("jvm-cli-app-java-min") {
            requiresJvm(11)
            expectedOutput("JVM: 11.")
        }
        derive("jvm-cli-app-java-max") {
            requiresJvm(26)
            expectedOutput("JVM: 26.")
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

    jvmCliApp("parse-jvm-cli-app") {
        cliArgs("100 + 200")
        expectedOutput("100 + 200 = 300")
        deriveNative("parse-native-cli-app")
    }
    jvmCliApp("parse-toml-jvm-cli-app") {
        cliArgs("--file", "../parse-toml-jvm-cli-app/test.toml")
        expectedOutput("key-1 = value 1")
        deriveNative("parse-toml-native-cli-app")
    }
    jvmCliApp("parse-exe-jvm-cli-app") {
        cliArgs("--help")
        deriveNative("parse-exe-native-cli-app")
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
    jvmUiApp("jvm-ui-app-generated-source")

    nativeUiApp("native-ui-app") {
        derive("native-ui-app-customized") {
            launcher("App")
        }
    }
    nativeUiApp("native-ui-app-generated-source")
}
