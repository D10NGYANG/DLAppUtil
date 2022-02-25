plugins {
    id(Android.Plugin.library)
    id(Kotlin.Plugin.ID.android)
    id(Kotlin.Plugin.ID.kapt)
    id(Maven.Plugin.public)
}

group = "com.github.D10NG"
version = "2.0"

android {
    compileSdk = Project.compile_sdk

    defaultConfig {
        minSdk = Project.min_sdk
        targetSdk = Project.target_sdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(Kotlin.stdlib(kotlin_ver))
    // Android
    implementation(AndroidX.core_ktx("1.7.0"))
    implementation(AndroidX.appcompat("1.4.1"))
    implementation(Android.Google.material("1.5.0"))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // 单元测试（可选）
    testImplementation(Test.junit("4.13.2"))
    androidTestImplementation(AndroidX.Test.junit("1.1.3"))
    androidTestImplementation(AndroidX.Test.espresso_core("3.4.0"))

    // Coroutines
    implementation(Kotlin.Coroutines.core(kotlin_coroutines_ver))
    implementation(Kotlin.Coroutines.android(kotlin_coroutines_ver))

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // Preference DataStore
    implementation(AndroidX.Datastore.datastore_preferences("1.0.0"))

    // 协程请求权限
    implementation("com.sagar:coroutinespermission:2.0.3")
}