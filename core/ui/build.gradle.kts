plugins {
    id("template.android.library.compose")
}

android {
    namespace = "com.template.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    implementation(projects.core.model)

    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
    implementation(libs.coil.compose)
}
