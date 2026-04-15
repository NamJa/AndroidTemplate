plugins {
    id("template.android.application")
    id("template.android.application.compose")
    id("template.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.template.app"

    defaultConfig {
        applicationId = "com.template.app"
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core.data)
    implementation(projects.core.network)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.ui)

    implementation(projects.feature.home)
    implementation(projects.feature.settings)

    implementation(libs.activity.compose)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.nav3.runtime)
    implementation(libs.nav3.ui)
    implementation(libs.lifecycle.viewmodel.nav3)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.core.ktx)
    implementation(libs.timber)
}
