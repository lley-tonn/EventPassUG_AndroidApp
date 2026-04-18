plugins {
    id("eventpass.android.library")
}

android {
    namespace = "com.eventpass.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
