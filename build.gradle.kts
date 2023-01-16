import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    application
    `maven-publish`
}

group = "org.scarf"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.charleskorn.kaml:kaml:0.49.0")
    implementation("com.github.sya-ri:kgit:1.0.5")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("org.slf4j:slf4j-simple:2.0.6")
//    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.19.0")

    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")

    testImplementation(kotlin("test"))

    val kotest = "5.5.4"
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotest")
    testImplementation("io.kotest:kotest-property:$kotest")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.scarf005"
            artifactId = "changelog"
            version = "0.0-SNAPSHOT"
            
            from(components["kotlin"])
        }
    }
}
