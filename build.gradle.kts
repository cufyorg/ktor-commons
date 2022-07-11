import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("maven-publish")
}

group = "org.cufy"
version = "1.0.0"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.0.2")

    implementation("org.litote.kmongo:kmongo:4.6.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.6.0")
    implementation("org.cufy:mangaka:1.0.0-beta.2")

    implementation("com.graphql-java:graphql-java:18.1")
    implementation("org.cufy:kaguya:1.0.0-beta.2")

    implementation("org.cufy:openperm-kt:1.0.0-beta.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                artifactId = "ktor-commons"
            }
        }
    }
}
