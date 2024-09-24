
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.licenser)
    id("maven-publish")
}


group = "dev.nikdekur"
version = "1.1.0"

val authorId: String by project
val authorName: String by project

allprojects {
    apply {
        plugin(rootProject.libs.plugins.kotlinJvm.get().pluginId)
        plugin(rootProject.libs.plugins.kotlinSerialization.get().pluginId)
        plugin(rootProject.libs.plugins.shadowJar.get().pluginId)
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

        testImplementation(kotlin("test"))
        testImplementation("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
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

    tasks.test {
        useJUnitPlatform()
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

if (repoUsername.isNullOrBlank() || repoPassword.isNullOrBlank())
    throw GradleException("Environment variables $repoUsernameProp and $repoPasswordProp must be set.")

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


            from(components["kotlin"])

            afterEvaluate {

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



@Suppress("NOTHING_TO_INLINE")
inline fun ShadowJar.reloc(path: String) {
    val name = path.substringAfterLast(".")
    val newPath = "dev.nikdekur.minelib.shaded.$name"
    println("Relocating `$path` to `$newPath`")
    relocate(path, newPath)
}

// Основная конфигурация
tasks.withType<ShadowJar> {
    archiveClassifier.set("")

    // Include all output directories and runtime classpath from all subprojects
    allprojects.forEach { project ->
        from(project.sourceSets.main.get().output)
        configurations.add(project.configurations.runtimeClasspath.get())
    }

//    println("Relocating dependencies")
//
//    // Shared
//    reloc("de.tr7zw.changeme.nbtapi")
//    reloc("dev.nikdekur.ndkore")
//    reloc("org.koin")
//
//    // Core
//    reloc("co.touchlab.stately")
//    reloc("com.charleskorn.kaml")
//    reloc("com.google")
//    reloc("com.benasher44.uuid")
//    reloc("javax.annotation")
//    reloc("kotlinx")
//    reloc("org.checkerframework")
//    reloc("org.snakeyaml")
//    reloc("org.intellij")
//    reloc("org.jetbrains")

    reloc("de.tr7zw.changeme.nbtapi")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
