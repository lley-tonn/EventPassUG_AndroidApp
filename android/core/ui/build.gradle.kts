plugins {
    id("eventpass.android.library")
    id("eventpass.android.compose")
}

android {
    namespace = "com.eventpass.core.ui"
}

dependencies {
    implementation(project(":core:design"))
    implementation(project(":core:common"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.coil.compose)
}
