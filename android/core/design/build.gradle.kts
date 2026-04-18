plugins {
    id("eventpass.android.library")
    id("eventpass.android.compose")
}

android {
    namespace = "com.eventpass.core.design"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.ui.text.google.fonts)
}
