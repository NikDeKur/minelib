import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.libs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.licenser)
    id("maven-publish")
}


group = "dev.nikdekur"
version = "1.0.0"

val authorId: String by project
val authorName: String by project

allprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlinJvm.get().pluginId)
        plugin(rootProject.libs.plugins.kotlinSerialization.get().pluginId)
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven {
            url = uri("https://repo.dmulloy2.net/repository/public/")
        }
        maven {
            name = "DestroyStokyo"
            url = uri("https://repo.destroystokyo.com/repository/maven-public/")
        }
        maven {
            name = "CodeMC"
            url = uri("https://repo.codemc.io/repository/maven-public/")
        }
        maven("https://repo.aikar.co/nexus/content/repositories/aikar-release/")
    }

    val implementation by configurations

    dependencies {
        implementation(rootProject.libs.ndkore)
        implementation(rootProject.libs.koin)
    }

    // Remove Java compatibility made by params non-null assertions
    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xno-param-assertions", "-Xno-call-assertions")
        }
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        jvmToolchain(8)
    }
}




tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    // Include all output directories and runtime classpath from all subprojects
    allprojects.forEach { project ->
        from(project.sourceSets.main.get().allSource)
    }
}


license {
    header(project.file("HEADER"))
    properties {
        set("year", "2024-present")
        set("name", authorName)
    }
}


val repoUsernameProp = "NDK_REPO_USERNAME"
val repoPasswordProp = "NDK_REPO_PASSWORD"
val repoUsername = System.getenv(repoUsernameProp)
val repoPassword = System.getenv(repoPasswordProp)

if (repoUsername.isNullOrBlank() || repoPassword.isNullOrBlank()) {
    throw GradleException("Environment variables $repoUsernameProp and $repoPasswordProp must be set.")
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

            afterEvaluate {
                val shadowJar = tasks.findByName("shadowJar")
                println(shadowJar)
                if (shadowJar == null) from(components["java"])
                else artifact(shadowJar)

                // Source jar
                artifact(tasks.named("sourcesJar", Jar::class.java))
            }
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "ndk-repo"
            url = uri("https://repo.nikdekur.tech/releases")
            credentials {
                username = repoUsername
                password = repoPassword
            }
        }

    }
}

tasks.test {
    useJUnitPlatform()
}



// Collect all in 1 jar
tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    // Include all output directories and runtime classpath from all subprojects
    allprojects.forEach { project ->
        from(project.sourceSets.main.get().output)
        configurations.add(project.configurations.runtimeClasspath.get())
    }

    relocate("de.tr7zw.changeme.nbtapi", "dev.nikdekur.nbtapi")
    relocate("kotlin", "dev.nikdekur.kotlin")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
