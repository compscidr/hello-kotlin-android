# hello-kotlin-android
[![Build Status](https://github.com/compscidr/hello-kotlin-android/workflows/Gradle%20Build/badge.svg)](https://github.com/compscidr/hello-kotlin-android/actions)&nbsp;
[![codecov](https://codecov.io/gh/compscidr/hello-kotlin-android/branch/main/graph/badge.svg?token=KQYUqNQ3Da)](https://codecov.io/gh/compscidr/hello-kotlin-android)&nbsp;
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)&nbsp;
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

When starting a new project, I always find getting the tools up and running quickly is a pain, so I
wanted something for Kotlin which already had everything setup and working.

This aims to be a starter repo with all of the following tooling setup:
* [Junit5](https://junit.org/junit5/docs/current/user-guide/)
* [Jacoco](https://www.eclemma.org/jacoco/) 
* [Github actions](https://github.com/marketplace/actions/gradle-android)
* [Codecov](https://codecov.io/)
* [ktlint](https://ktlint.github.io/)
* [Mockk](https://mockk.io/ANDROID.html)
* [Dokka](https://github.com/Kotlin/dokka)
* [r8](https://android-developers.googleblog.com/2018/11/r8-new-code-shrinker-from-google-is.html)
* [Dagger2](https://medium.com/@elye.project/dagger-2-for-dummies-in-kotlin-with-one-page-simple-code-project-618a5f9f2fe8)
* [Logback Android](https://github.com/tony19/logback-android)
* [Papertrail](https://papertrailapp.com/)
* Unit test examples
* Instrumented test examples

## Secrets
Secrets are passed in via `local.properties` or `.env` or via environment variables. There is an example in the GH workflows which shows how to pass in via
`local.properties` and via environment variables. It will also work with a `.env` in the app directory.

## Note about squashing
Do not do squash merges to the staging or production branches, or in the mergebacks to main, or
there will be nothing but trouble. See the following for more details:
https://github.com/orgs/community/discussions/10809

## Staging release
https://github.com/compscidr/hello-kotlin-android/compare/staging...main

## Production release
https://github.com/compscidr/hello-kotlin-android/compare/production...staging

## Merge-backs
NOTE: the mergeback should be a merge commit, not a squash commit or will result in a diff when
comparing branches later on.

Ideally you find no diff:
https://github.com/compscidr/hello-kotlin-android/compare/main...production
https://github.com/compscidr/hello-kotlin-android/compare/main...staging
https://github.com/compscidr/hello-kotlin-android/compare/staging...production

TODO:
* [Firebase](https://firebase.google.com/docs/crashlytics)
