group = "dev.nikdekur"
version = "1.0.0"

dependencies {
    implementation(libs.google.guava)
    implementation(libs.kaml)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlin.logging)
    implementation(libs.bignum)
    implementation(libs.nbtapi)

    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    compileOnly(libs.authlib)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.kotlin.logging)
}