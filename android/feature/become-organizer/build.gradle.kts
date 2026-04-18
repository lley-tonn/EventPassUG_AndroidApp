plugins {
    id("eventpass.android.library")
    id("eventpass.android.compose")
    id("eventpass.android.hilt")
}

android {
    namespace = "com.eventpass.feature.becomeorganizer"
}

dependencies {
    implementation(project(":core:design"))
    implementation(project(":core:ui"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.view)
    implementation(libs.mlkit.text.recognition)
}
