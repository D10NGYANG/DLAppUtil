plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.d10ng.app.demo"
    compileSdk = android_compile_sdk

    defaultConfig {
        applicationId = "com.d10ng.app.demo"
        minSdk = android_min_sdk
        targetSdk = android_target_sdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation("androidx.core:core-ktx:$androidx_core_ver")

    // 单元测试（可选）
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlin_coroutines_ver")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlin_coroutines_ver")

    // 导航路由
    implementation("io.github.raamcosta.compose-destinations:animations-core:$compose_destinations_ver")
    ksp("io.github.raamcosta.compose-destinations:ksp:$compose_destinations_ver")

    // jetpack compose 框架
    implementation("com.github.D10NGYANG:DLJetpackComposeUtil:$dl_compose_ver")

    // 日期时间
    implementation("com.github.D10NGYANG:DLDateUtil:$dl_date_ver")
    // 通用计算工具
    implementation("com.github.D10NGYANG:DLCommonUtil:$dl_common_ver")

    // 工具
    implementation(project(":library"))

    // 内存泄漏检查
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}