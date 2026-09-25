// Baseline-profile GENERATOR module. Runs the app on a device, records the classes/methods the
// hot paths touch (startup, map pan, settings scroll), and writes them to
// app/src/release/generated/baselineProfiles/ - which is COMMITTED, so every CI release build
// bakes it and androidx.profileinstaller AOT-compiles those paths at install time. Sideloaded
// installs (Obtainium) get no Play cloud profiles; without this every nightly ran
// interpreter-cold until overnight background dexopt.
//
// Regenerate (any API 33+ device, e.g. the wired test phone; release keystore env so the
// nonMinified variant installs over the existing app):
//   VELA_KEYSTORE_PATH=... VELA_KEYSTORE_PASSWORD=... VELA_KEY_ALIAS=vela \
//     ./gradlew :app:generateBaselineProfile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.test)
    // AGP 9.0 已内置 Kotlin，不再需要 kotlin.android
    // alias(libs.plugins.kotlin.android)   ← 删除
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "app.vela.baselineprofile"
    compileSdk = 36   // 与主项目对齐
    defaultConfig {
        minSdk = 28   // 宏基准测试要求 API 28+
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // AGP 9.0: kotlinOptions 已移除，jvmTarget 通过顶层 kotlin.compilerOptions 配置（见文件末尾）
    targetProjectPath = ":app"

    testOptions.managedDevices.localDevices.create("pixel6Api34") {
        device = "Pixel 6"
        apiLevel = 34
        systemImageSource = "aosp"
    }
}

// Generation runs on a Gradle-managed EMULATOR, never a connected phone: the test harness
// UNINSTALLS the target app when it finishes, which on a real device nukes saved places, trips
// and permission grants (it did, once - 2026-07-16). An emulator is disposable, and the same
// invocation runs headless in CI (ubuntu runners have KVM).
baselineProfile {
    managedDevices += "pixel6Api34"
    useConnectedDevices = false
}

dependencies {
    implementation(libs.androidx.test.junit)   // 原 libs.androidx.junit
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro)
}

// AGP 9.0: Kotlin 编译器选项通过顶层 kotlin 块配置
kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}