오늘의 목표: 내부지도 핀치 확대 및 축소 기능 도입

세부사항:

-내부 안내 확대 기능 코드 변경:
<xml파일>
기존 activity_beacon_image.xml파일의 이미지 부분의 코드를 다음과 같이 변경
<com.github.chrisbanes.photoview.PhotoView
android:id="@+id/imageViewBeacon"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:layout_below="@id/searchLayout"
android:layout_marginTop="30dp"
android:src="@drawable/image1" />

<그래들:프로젝트 단위>
allprojects의 repositories에 다음과 같이 maven을 추가
allprojects {
repositories {
google()
mavenCentral()
maven("https://repository.map.naver.com/archive/maven/")
maven("https://jitpack.io")  //핀치줌 사용을 위해 추가
}
}

<그래들:앱 단위>
기존 앱 단위 그래들 코드에서 다음 의존성 코드를 추가
implementation ("com.github.chrisbanes:PhotoView:2.3.0") //이미지 핀치 줌,아웃을 위한 의존성
