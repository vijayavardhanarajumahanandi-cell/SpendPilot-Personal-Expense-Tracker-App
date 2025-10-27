// ✅ Project-level build.gradle.kts

plugins {
    // Core build plugins (from libs.versions.toml)
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.dagger.hilt) apply false

    // Firebase & Google Services
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
}
