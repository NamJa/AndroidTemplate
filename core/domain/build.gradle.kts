plugins {
    id("template.jvm.library")
}

dependencies {
    implementation(projects.core.model)
    implementation(libs.coroutines.android)
    implementation(libs.javax.inject)
}
