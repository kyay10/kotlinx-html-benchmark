import kotlinx.benchmark.gradle.JavaBenchmarkTarget

plugins {
    kotlin("jvm") version "1.8.10"
    application
    id("org.jetbrains.kotlinx.benchmark") version "0.4.7"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.8.10"
}

group = "io.github.kyay10"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
}

dependencies {
    testImplementation(kotlin("test"))
    // include for JVM target
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime:0.4.7")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
    target.compilations {
        val main by getting
        benchmark.targets.add(
            JavaBenchmarkTarget(
                benchmark,
                main.defaultSourceSet.name,
                main.javaSourceSet,
            ).apply {
                jmhVersion = "1.21"
            }
        )
    }
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

application {
    mainClass.set("MainKt")
}