plugins {
    alias(libs.plugins.hivemq.extension)
    alias(libs.plugins.defaults)
    alias(libs.plugins.oci)
    alias(libs.plugins.license)
}

group = "com.hivemq.extensions"
description = "HiveMQ Heartbeat Extension - Provides a readiness check via HTTP"

hivemqExtension {
    name = "HiveMQ Heartbeat Extension"
    author = "HiveMQ"
    priority = 1000
    startPriority = 1000
    sdkVersion = libs.versions.hivemq.extensionSdk

    resources {
        from("LICENSE")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    implementation(libs.commonsLang)
    implementation(libs.commonsText)
    implementation(libs.jaxb.api)
    runtimeOnly(libs.jaxb.impl)
    implementation(libs.jetty.server)
    implementation(libs.jetty.servlet)
}

oci {
    registries {
        dockerHub {
            optionalCredentials()
        }
    }
    imageMapping {
        mapModule("com.hivemq", "hivemq-community-edition") {
            toImage("hivemq/hivemq-ce")
        }
    }
    imageDefinitions.register("main") {
        allPlatforms {
            dependencies {
                runtime("com.hivemq:hivemq-community-edition:latest") { isChanging = true }
            }
            config {
                ports = setOf("9090")
            }
            layer("main") {
                contents {
                    permissions("opt/hivemq/", 0b111_111_101)
                    permissions("opt/hivemq/extensions/", 0b111_111_101)
                    into("opt/hivemq/extensions") {
                        from(zipTree(tasks.hivemqExtensionZip.flatMap { it.archiveFile }))
                    }
                }
            }
        }
    }
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(libs.versions.junit.jupiter)
        }
        "test"(JvmTestSuite::class) {
            dependencies {
                compileOnly(libs.jetbrains.annotations)
                implementation(libs.mockito)
            }
        }
        "integrationTest"(JvmTestSuite::class) {
            dependencies {
                compileOnly(libs.jetbrains.annotations)
                implementation(libs.hivemq.mqttClient)
                implementation(libs.testcontainers.junitJupiter)
                implementation(libs.testcontainers.hivemq)
                implementation(libs.gradleOci.junitJupiter)
                implementation(libs.okhttp)
                runtimeOnly(libs.logback.classic)
            }
            oci.of(this) {
                imageDependencies {
                    runtime(project).tag("latest")
                }
            }
        }
    }
}

license {
    header = rootDir.resolve("HEADER")
    mapping("java", "SLASHSTAR_STYLE")
}
