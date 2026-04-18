plugins {
    id("eventpass.android.library")
    id("eventpass.android.compose")
}

android {
    namespace = "com.eventpass.feature.auth"
}

dependencies {
    implementation(project(":core:design"))
    implementation(libs.androidx.core.ktx)
}
