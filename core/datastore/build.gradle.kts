plugins {
    id("template.android.library")
    id("template.android.hilt")
}

android {
    namespace = "com.template.core.datastore"
}

dependencies {
    implementation(projects.core.common)

    implementation(libs.datastore.preferences)
    implementation(libs.coroutines.android)
}
