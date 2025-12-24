import io.github.reactivecircus.appversioning.SemVer
import java.io.*
import java.io.FileInputStream
import java.util.*

fun getLocalProperty(key: String, file: String = project.rootDir.toString() + "/local.properties"): String? {
    val properties = Properties()
    val localProperties = File(file)
    if (localProperties.isFile) {
        InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
            properties.load(reader)
        }
    } else {
        return null
    }

    return properties.getProperty(key)
}

fun propertyEnvOrEmpty(key: String): String {
    return getLocalProperty(key) ?: env.fetch(key, "")
}

/**
 * Ensures that the string is enclosed with quotes so that in the BuildConfig.java file, it doesn't
 * leave a blank which isn't compilable.
 */
fun buildConfigString(key: String): String {
    return "\"${propertyEnvOrEmpty(key)}\""
}

plugins {
    id("jacoco")
    id("maven-publish")
    alias(libs.plugins.android.application)
    alias(libs.plugins.app.versioning)
    alias(libs.plugins.de.mannodermaus.android.junit5)
    alias(libs.plugins.dokka)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlinter)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26 // required for mockk-android 1.14.7+
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"

        buildConfigField("String", "ENV_SECRET", buildConfigString("ENV_SECRET"))
        buildConfigField("String", "PROPERTY_SECRET", buildConfigString("PROPERTY_SECRET"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
        }
        debug {
            isShrinkResources = false
            isMinifyEnabled = false
            enableAndroidTestCoverage = true
            enableUnitTestCoverage = true
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        configure<JacocoTaskExtension> {
            // Required for JaCoCo + Robolectric
            // https://github.com/robolectric/robolectric/issues/2230
            isIncludeNoLocationClasses = true

            // Required for JDK 11 with the above
            // https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
            excludes = listOf("jdk.internal.*")
        }
        finalizedBy("jacocoTestReport")
    }

    buildFeatures {
        buildConfig = true
    }
}

configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin" && requested.name == "kotlin-metadata-jvm") {
            useVersion("2.3.0")
            because("Force kotlin-metadata-jvm to support Kotlin 2.3.0 until Hilt officially supports it")
        }
    }
}

junitPlatform {
    // this is for the non-android unit tests, only required with the mannodermaus plugin
    jacocoOptions {
        html.enabled = true
        xml.enabled = true
        csv.enabled = false
    }
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.androidx.multidex)
    implementation(libs.androidx.appcompat)
    implementation(libs.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.bundles.test.runtime)

    androidTestImplementation(libs.bundles.android.test)
    androidTestRuntimeOnly(libs.de.manodermaus.android.junit5.runner)

    api(libs.slf4j.api)
    implementation(libs.logback.android)
    implementation(libs.logback.papertrail) {
        exclude(group = "ch.qos.logback")
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.withType(JacocoReport::class.java).configureEach {
    executionData(fileTree("build/outputs/code_coverage/debugAndroidTest/connected/").include("**/*.ec"))
    executionData(fileTree("build/outputs/unit_test_code_coverage/debugUnitTest/").include("**/*.exec"))
}

kotlin {
    jvmToolchain(17)
}

// https://github.com/ReactiveCircus/app-versioning will set:
// - BuildConfig.VERSION_CODE to the semver version code
// - Buildconfig.VERSION_NAME to the git tag
appVersioning {
    fetchTagsWhenNoneExistsLocally.set(true)
    overrideVersionCode { gitTag, _, _ ->
        val semVer = SemVer.fromGitTag(gitTag)
        semVer.major * 2000000 + semVer.minor * 20000 + semVer.patch * 200 + gitTag.commitsSinceLatestTag
    }
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/compscidr/hello-kotlin-android")
            credentials {
                username = propertyEnvOrEmpty("GH_USER")
                password = propertyEnvOrEmpty("GH_PAT")
            }
        }
        maven {
            name = "CustomMavenRepo"
            url = uri("file://${layout.buildDirectory.get().asFile}/repo")
        }
    }
    publications {
        // in theory each of these artifacts might have different compiled in secrets or something
        // to make them different, but for now they're all the same just to test the workflow
        register("dev", MavenPublication::class) {
            groupId = "com.example"
            artifactId = "myapplication.dev"
            version = propertyEnvOrEmpty("VERSION_NAME") + "-" + propertyEnvOrEmpty("VERSION_CODE")
            artifact(layout.buildDirectory.file("outputs/apk/release/app-release-unsigned.apk"))
            artifact(layout.buildDirectory.file("outputs/bundle/release/app-release.aab"))
        }
        register("staging", MavenPublication::class) {
            groupId = "com.example"
            artifactId = "myapplication.staging"
            version = propertyEnvOrEmpty("VERSION_NAME") + "-" + propertyEnvOrEmpty("VERSION_CODE")
            artifact(layout.buildDirectory.file("outputs/apk/release/app-release-unsigned.apk"))
            artifact(layout.buildDirectory.file("outputs/bundle/release/app-release.aab"))
        }
        register("production", MavenPublication::class) {
            groupId = "com.example"
            artifactId = "myapplication"
            version = propertyEnvOrEmpty("VERSION_NAME") + "-" + propertyEnvOrEmpty("VERSION_CODE")
            artifact(layout.buildDirectory.file("outputs/apk/release/app-release-unsigned.apk"))
            artifact(layout.buildDirectory.file("outputs/bundle/release/app-release.aab"))
        }
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn("assembleRelease")
    dependsOn("bundleRelease")
}
