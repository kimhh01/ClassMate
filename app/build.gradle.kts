import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.navermapsample"
    compileSdk = 35

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.navermapsample"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        packagingOptions {
            exclude ("META-INF/NOTICE.md")
            exclude ("META-INF/LICENSE.md")

            // 필요한 경우 추가적으로 다른 파일도 제외할 수 있습니다.
        }
        // --- local.properties 파일에서 속성 읽어오기 ---
        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            propertiesFile.inputStream().use { properties.load(it) }
        } else {
            // local.properties 파일이 없을 경우 빌드 실패 또는 경고 처리 (선택 사항)
            logger.warn("local.properties 파일이 프로젝트 루트에 없습니다. API 키가 누락될 수 있습니다.")
            // 또는 에러 발생 시키기: throw GradleException("local.properties 파일이 없습니다.")
        }
        // --- 속성 읽어오기 끝 ---


        // --- BuildConfig 필드 정의 (읽어온 속성 사용) ---
        // properties.getProperty("키 이름", "기본값") 함수를 사용하여 값 가져오기
        // 값은 Java/Kotlin 코드에서 문자열 리터럴 형태("값")가 되어야 하므로 "\"값\"" 형식으로 지정
        buildConfigField(
            "String",
            "NAVER_CLIENT_ID",
            "\"${properties.getProperty("NAVER_CLIENT_ID", "")}\"" // 키가 없을 경우 빈 문자열 "" 반환
        )
        buildConfigField(
            "String",
            "NAVER_CLIENT_SECRET",
            "\"${properties.getProperty("NAVER_CLIENT_SECRET", "")}\"" // 키가 없을 경우 빈 문자열 "" 반환
        )
        buildConfigField(
            "String",
            "OPENWEATHER_API_KEY",
            "\"${properties.getProperty("OPENWEATHER_API_KEY", "")}\"" // 키가 없을 경우 빈 문자열 "" 반환
        )
        buildConfigField(
            "String",
            "NAVER_SEARCH_CLIENT_ID",
            "\"${properties.getProperty("NAVER_SEARCH_CLIENT_ID", "")}\""
        )
        buildConfigField(
            "String",
            "NAVER_SEARCH_CLIENT_SECRET",
            "\"${properties.getProperty("NAVER_SEARCH_CLIENT_SECRET", "")}\"")
        // --- BuildConfig 필드 정의 끝 ---

        // --- Manifest 플레이스홀더 정의 (AndroidManifest.xml에 사용) ---
        // AndroidManifest.xml의 <meta-data> 태그에서 사용할 플레이스홀더를 정의합니다.
        manifestPlaceholders["naverMapsClientId"] = properties.getProperty("NAVER_CLIENT_ID", "")
        // --- Manifest 플레이스홀더 정의 끝 ---
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = "1.4.3"
//    }
//    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
}

dependencies {
    implementation("org.greenrobot:eventbus:3.3.1")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("com.naver.maps:map-sdk:3.17.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.google.android.material:material:1.12.0")
    implementation(files("libs\\minewBeaconAdmin.jar"))
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.gms:play-services-maps:19.1.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.27")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.sun.mail:android-mail:1.6.7")
    implementation ("com.sun.mail:android-activation:1.6.7")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("com.airbnb.android:lottie:6.0.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.2") // 디버깅용
    implementation ("com.github.chrisbanes:PhotoView:2.3.0")
    implementation ("androidx.gridlayout:gridlayout:1.1.0")
    implementation(fileTree("libs") {
        include("*.jar")
    })
    implementation ("com.github.tlaabs:timetableview:1.0.1")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")

}