plugins {
    id("eventpass.android.library")
    id("eventpass.android.compose")
    id("eventpass.android.hilt")
}

android {
    namespace = "com.eventpass.feature.attendee"
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
    implementation(libs.coil.compose)
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.pay)
    implementation(libs.zxing.core)
}
