plugins {
    id("template.android.feature")
}

android {
    namespace = "com.template.feature.home"

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.ui)
    implementation(projects.core.designsystem)

    testImplementation(projects.core.testing)
    testImplementation(libs.junit)
}
