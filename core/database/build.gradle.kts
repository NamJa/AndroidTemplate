plugins {
    id("template.android.library")
    id("template.android.hilt")
    id("template.android.room")
}

android {
    namespace = "com.template.core.database"
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.coroutines.android)
}
