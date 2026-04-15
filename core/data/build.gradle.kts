plugins {
    id("template.android.library")
    id("template.android.hilt")
}

android {
    namespace = "com.template.core.data"
}

dependencies {
    api(projects.core.model)
    api(projects.core.domain)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.datastore)

    implementation(libs.coroutines.android)
}
