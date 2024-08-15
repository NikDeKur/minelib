group = "dev.nikdekur.minelib"
version = "1.0.0"

dependencies {
    implementation(libs.google.guava)
    implementation(libs.kaml)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.nbtapi)
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly(libs.authlib)
}