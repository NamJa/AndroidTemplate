plugins {
    id("template.android.library")
    id("template.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.template.core.network"
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"https://api.template.com/\"")
    }
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.common)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.android)
    implementation(libs.timber)
    api(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
