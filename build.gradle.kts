plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.app.versioning) apply false
    alias(libs.plugins.dotenv)
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlinter) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.de.mannodermaus.android.junit5) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlin.ksp) apply false
}