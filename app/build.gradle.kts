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
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 23 // with 21 we get a deprecation warning
        targetSdk = 34
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

//    testOptions {
//        unitTests.all {
//            useJUnitPlatform()
//            jacoco {
//                includeNoLocationClasses = true
//                excludes = [
//                        'jdk.internal.*',
//                ]
//            }
//        }
//        // should let the tests run with with gradle via -DincludeTags="tag1,tag2" -DexcludeTags="tag3,tag4"
//        // https://github.com/mannodermaus/android-junit5/wiki/Configuration
//        // https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering
//        // https://www.vogella.com/tutorials/AndroidTesting/article.html
//        junitPlatform {
//            filters {
//                //includeTags 'TEST1'
//                //excludeTags 'TEST2'
//            }
//        }
//        // should let the tests run with with gradle via -DincludeTags="tag1,tag2" -DexcludeTags="tag3,tag4"
//        // https://github.com/mannodermaus/android-junit5/wiki/Configuration
//        // https://junit.org/junit5/docs/current/user-guide/#writing-tests-tagging-and-filtering
//        // https://www.vogella.com/tutorials/AndroidTesting/article.html
//        junitPlatform {
//            String includedTagsProperty = System.getProperty("includeTags")
//            boolean includedTagsPropertySet = includedTagsProperty != null
//
//            String excludedTagsProperty = System.getProperty("excludeTags")
//            boolean excludedTagsPropertySet = excludedTagsProperty != null
//
//            if (includedTagsPropertySet && excludedTagsPropertySet) {
//                filters("debug") {
//                    includeTags includedTagsProperty
//                    excludeTags excludedTagsProperty
//                }
//            } else if (includedTagsPropertySet && !excludedTagsPropertySet) {
//                filters("debug") {
//                    includeTags includedTagsProperty
//                }
//            } else if (!includedTagsPropertySet && excludedTagsPropertySet) {
//                filters("debug") {
//                    excludeTags excludedTagsProperty
//                }
//            }
//        }
//    }
    buildFeatures {
        buildConfig = true
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

    testImplementation(libs.bundles.test)
    testRuntimeOnly(libs.junit.jupiter.engine)

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

tasks.withType(JacocoReport::class.java) {
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

// for some reason running the jacocoTestReport generates a report for the release unit tests that
// works with jacoco, however it doesn't add it to the
//tasks.create("jacocoTestReportUnitTests", type: JacocoReport, dependsOn: "jacocoTestReport") {
//    group = "Reporting" // existing group containing tasks for generating linting reports etc.
//    description = "Generate JVM unit test coverage reports using Jacoco."
//
//    reports {
//        // human readable (written into './build/reports/jacoco/unitTestCoverageReport/html')
//        html.required = true
//        // CI-readable (written into './build/reports/jacoco/unitTestCoverageReport/unitTestCoverageReport.xml')
//        xml.required = true
//    }
//
//    // Execution data generated when running the tests against classes instrumented by the JaCoCo agent.
//    // This is enabled with 'enableUnitTestCoverage' in the 'debug' build type.
//    executionData.from = fileTree(dir: "${project.buildDir}/jacoco/", includes: ["/testReleaseUnitTest.exec"])
//
//    // Compiled Kotlin class files are written into build-variant-specific subdirectories of 'build/tmp/kotlin-classes'.
//    classDirectories.from = "${project.buildDir}/tmp/kotlin-classes/release"
//
//    // To produce an accurate report, the bytecode is mapped back to the original source code.
//    sourceDirectories.from = "${project.projectDir}/src/main/java"
//}