plugins {
    id("eventpass.android.library")
    id("eventpass.android.compose")
}

android {
    namespace = "com.eventpass.feature.onboarding"
}

dependencies {
    implementation(project(":core:design"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
