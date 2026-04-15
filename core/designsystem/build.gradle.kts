plugins {
    id("template.android.library.compose")
}

android {
    namespace = "com.template.core.designsystem"
}

dependencies {
    implementation(libs.compose.material3)
    implementation(libs.compose.runtime)
}
