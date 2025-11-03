plugins {
    kotlin("jvm")
}

group = "org.jetbrains.kotlinx.tictactoe"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}