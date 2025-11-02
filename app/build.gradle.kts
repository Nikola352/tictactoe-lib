plugins {
    kotlin("jvm")
    application
}
dependencies {
    implementation(project(":lib"))
    implementation(project(":lib-players"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

application {
    mainClass = "org.jetbrains.kotlinx.tictactoe.MainKt"
}
