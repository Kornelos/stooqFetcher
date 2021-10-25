import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kotest_version: String by project
val kotest_extensions_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
}

group = "com.kornelos"
version = "0.0.1"
application {
    mainClass.set("com.kornelos.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-gson:$ktor_version")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotest_version")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:$kotest_extensions_version")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.create("stage") {
    dependsOn("installDist")
}