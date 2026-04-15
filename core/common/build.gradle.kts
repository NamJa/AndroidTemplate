plugins {
    id("template.android.library")
    id("template.android.hilt")
}

android {
    namespace = "com.template.core.common"
}

dependencies {
    implementation(libs.coroutines.android)
    implementation(libs.timber)
}
