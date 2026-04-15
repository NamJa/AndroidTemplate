plugins {
    id("template.android.feature")
}

android {
    namespace = "com.template.feature.settings"

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)

    testImplementation(projects.core.testing)
    testImplementation(libs.junit)
}
