
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.compileOnly
import org.gradle.kotlin.dsl.libs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.shadowJar)
    id("maven-publish")
}


group = "dev.nikdekur"
version = "1.0.0"

val authorId: String by project
val authorName: String by project

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.destroystokyo.com/repository/maven-public/")
    }
}

dependencies {
    compileOnly(libs.spigot)
    implementation(libs.ndkore)
    implementation(libs.google.guava)
    implementation(libs.kaml)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll("-Xno-param-assertions", "-Xno-call-assertions")
    }
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            pom {
                developers {
                    developer {
                        id.set(authorId)
                        name.set(authorName)
                    }
                }
            }

            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
}