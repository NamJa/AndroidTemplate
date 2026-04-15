plugins {
    id("template.android.library")
}

android {
    namespace = "com.template.core.testing"
}

dependencies {
    api(projects.core.model)
    api(projects.core.domain)

    api(libs.junit)
    api(libs.coroutines.test)
    api(libs.turbine)
    api(libs.truth)
    api(libs.mockk)
}
