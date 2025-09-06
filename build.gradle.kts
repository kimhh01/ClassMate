buildscript {
    repositories {
        google()
        mavenCentral()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.4.1")
        classpath(kotlin("gradle-plugin:1.8.20"))
        classpath("com.google.gms:google-services:4.4.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
//        maven("https://naver.jfrog.io/artifactory/maven/")
        maven("https://repository.map.naver.com/archive/maven/")
    }
}


