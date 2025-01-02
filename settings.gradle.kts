plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "minelib"

dependencyResolutionManagement {

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
}

include("core")
include("versions:1_12_R1")
include("versions:1_20_4")