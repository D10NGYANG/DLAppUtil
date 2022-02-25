plugins {
    id(Android.Plugin.application)
    id(Kotlin.Plugin.ID.android)
    id(Kotlin.Plugin.ID.kapt)
    id(Kotlin.Plugin.ID.parcelize)
}

android {
    compileSdk = Project.compile_sdk

    defaultConfig {
        applicationId = "com.d10ng.dlapputil"
        minSdk = Project.min_sdk
        targetSdk = Project.target_sdk
        versionCode = 1
        versionName = "1.0"

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

    // Android
    implementation(AndroidX.core_ktx("1.7.0"))
    implementation(AndroidX.appcompat("1.4.1"))
    implementation(Android.Google.material("1.5.0"))
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    // 单元测试（可选）
    testImplementation(Test.junit("4.13.2"))
    androidTestImplementation(AndroidX.Test.junit("1.1.3"))
    androidTestImplementation(AndroidX.Test.espresso_core("3.4.0"))

    // Coroutines
    implementation(Kotlin.Coroutines.core(kotlin_coroutines_ver))
    implementation(Kotlin.Coroutines.android(kotlin_coroutines_ver))

    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    kapt("androidx.lifecycle:lifecycle-common-java8:2.3.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    // ViewModel Kotlin support
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")

    // Preference DataStore
    implementation(AndroidX.Datastore.datastore_preferences("1.0.0"))

    // 协程请求权限
    implementation("com.sagar:coroutinespermission:2.0.3")

    // 字符串字节数据工具
    implementation(D10NG.DLTextUtil())

    implementation(project(":AppLib"))
}