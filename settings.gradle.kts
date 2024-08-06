plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "minelib"


include("core")
include("versions:1_12_R1")
include("versions:1_20_4")