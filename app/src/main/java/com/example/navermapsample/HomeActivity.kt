package com.example.navermapsample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.*
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.*
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class HomeActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var tvTime: TextView
    private lateinit var tvWeather: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var nicknameTextView: TextView
    private lateinit var tvLocation: TextView
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1002
    private val API_KEY = BuildConfig.OPENWEATHER_API_KEY






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        tvTime = findViewById(R.id.tv_time)
        tvWeather = findViewById(R.id.tv_weather)
        tvLocation = findViewById(R.id.tvLocation)

        val homeTitleTextView = findViewById<TextView>(R.id.homeTitle)
        nicknameTextView = findViewById(R.id.nicknameText)


        // SharedPreferences에서 닉네임 불러오기
        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val nickname = prefs.getString("nickname", "사용자") // 디폴트값은 "사용자"

        // 랜덤으로 나올 닉네임 뒤의 메시지
        val phrases = listOf(
            "님 무엇을 도와드릴까요?",
            "님 어떤 강의실을 찾으시나요?",
            "님 오늘의 수업은 어디일까요?",
            "님 좋은 하루 입니다!",
            "님 반갑습니다! ClassMate 입니다!",
            "님 오늘도 활기찬 하루 보내세요!",
            "님 오늘의 수업 위치를 알고 싶으신가요?"
        )

        val randomPhrase = phrases.random() // 리스트에서 랜덤으로 선택

        // 제목 텍스트와 닉네임 텍스트 설정
        homeTitleTextView.text = "클래스메이트 입니다!"
        nicknameTextView.text = "$nickname$randomPhrase"

        // 명언 가져오기
        getAdvice()
    }

    override fun onResume() {
        super.onResume()

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val nickname = prefs.getString("nickname", "사용자")
        val phrases = listOf(
            "님 무엇을 도와드릴까요?",
            "님 어떤 강의실을 찾으시나요?",
            "님 오늘의 수업은 어디일까요?",
            "님 좋은 하루 입니다!",
            "님 반갑습니다! ClassMate 입니다!",
            "님 오늘도 활기찬 하루 보내세요!",
            "님 오늘의 수업 위치를 알고 싶으신가요?"
        )
        val randomPhrase = phrases.random()
        nicknameTextView.text = "$nickname$randomPhrase" // 👈 다시 세팅

        checkBluetoothPermissions()
    }

    private fun checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                    ),
                    REQUEST_BLUETOOTH_PERMISSIONS
                )
            }
        }
    }


    private fun getAdvice() {
        RetrofitClient.apiService.getAdvice().enqueue(object : Callback<AdviceResponse> {
            override fun onResponse(call: Call<AdviceResponse>, response: Response<AdviceResponse>) {
                if (response.isSuccessful) {
                    val advice = response.body()
                    advice?.let {
                        // 명언 표시
                        val adviceTextView: TextView = findViewById(R.id.adviceTextView)
                        adviceTextView.text = "\"${it.message}\" - ${it.author} -"
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "명언을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AdviceResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "명언을 가져오는데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        })



        // 현재 시간 업데이트 (1분마다 실행)
        startUpdatingTime()

        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()

        // 지도 버튼
        val mapButton: Button = findViewById(R.id.mapButton)
        mapButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // 기존 MainActivity로 이동
            startActivity(intent)
        }

        // 시간표 버튼
        val timetableButton: ImageButton = findViewById(R.id.timetableButton)
        timetableButton.setOnClickListener {
            val intent = Intent(this, TimetableActivity::class.java)
            startActivity(intent)
        }

        // 강의실 찾기 버튼
        val roomFinderButton: Button = findViewById(R.id.roomFinderButton)
        roomFinderButton.setOnClickListener {
            showRoomFinderDialog()
        }

        //학사 공지사항 버튼
        val infoButton: ImageButton = findViewById(R.id.infoButton)
        infoButton.setOnClickListener {
            val intent = Intent(this, Info_main::class.java)
            startActivity(intent)
        }
        //도움말 버튼
        val helpButton: ImageButton = findViewById(R.id.helpButton)
        helpButton.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
        }

    }

    // 강의실 찾기 다이얼로그 표시 함수
    private fun showRoomFinderDialog() {
        // 다이얼로그를 위한 커스텀 레이아웃 생성
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_room_finder, null)

        // AlertDialog 생성
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // 바깥 클릭으로 닫히지 않도록 설정
            .create()

        // 닫기 버튼 설정
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeDialogButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // 자동으로 강의실 찾기 카드뷰
        val autoFindCardView = dialogView.findViewById<androidx.cardview.widget.CardView>(R.id.autoFindCardView)
        autoFindCardView.setOnClickListener {
            // 버튼 어둡게 표시 효과
            autoFindCardView.isPressed = true

            // 약간의 딜레이 후 액티비티 전환 (버튼 어두워짐 효과를 볼 수 있도록)
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
                dialog.dismiss()
            }, 150) // 150ms 딜레이
        }

        // 수동으로 강의실 찾기 카드뷰
        val manualFindCardView = dialogView.findViewById<androidx.cardview.widget.CardView>(R.id.manualFindCardView)
        manualFindCardView.setOnClickListener {
            // 버튼 어둡게 표시 효과
            manualFindCardView.isPressed = true

            // 약간의 딜레이 후 액티비티 전환 (버튼 어두워짐 효과를 볼 수 있도록)
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, QrCodeScanActivity::class.java)
                startActivity(intent)
                dialog.dismiss()
            }, 150) // 150ms 딜레이
        }

        // 다이얼로그 배경을 투명하게 설정하여 CardView의 둥근 모서리가 보이도록 함
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun startUpdatingTime() {
        updateTime() // 🔥 앱 실행 시 즉시 현재 시간 업데이트
        val updateRunnable = object : Runnable {
            override fun run() {
                updateTime()
                handler.postDelayed(this, 60)
            }
        }
        handler.postDelayed(updateRunnable, 60) // 🚀 1분 후부터 반복 실행
    }

    // 현재 시간 갱신
    private fun updateTime() {
        val currentTime = SimpleDateFormat("yyyy년 MM월 dd일 a hh:mm:ss", Locale.KOREAN).format(Date())
        tvTime.text = "🕒 $currentTime"
    }

    // 📍 위치 업데이트 요청
    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 60000 // 1분마다 위치 갱신
            fastestInterval = 30000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    fetchWeather(location.latitude, location.longitude)
                }
            }
        }

        // 권한 체크 후 위치 업데이트 요청
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    // 🌤️ 날씨 API 호출
    private fun fetchWeather(lat: Double, lon: Double) {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&appid=$API_KEY&units=metric&lang=En"
        Log.d("API KEY", API_KEY) // null 아니어야 해
        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response -> parseWeather(response, lat, lon) },
            { tvWeather.text = "☁❌" }
        )
        Volley.newRequestQueue(this).add(request)
    }

    // 🌡️ JSON 응답 파싱
    @SuppressLint("SetTextI18n")
    private fun parseWeather(response: JSONObject, lat1: Double, lon1: Double) {
        val temp = response.getJSONObject("main").getDouble("temp")  // 현재 온도
        val roundedTemp = temp.toInt()  // 🔹 소수점 제거
        val weatherDescription = response.getJSONArray("weather").getJSONObject(0).getString("description")  // 날씨 설명 (영어)
        Log.d("WeatherAPI", "날씨 상태 원본: $weatherDescription")  // 로그 출력

        // ✅ 날씨 상태 변환을 위한 매핑
        val weatherMap = mapOf(
            "clear sky" to "☀️ 맑음",
            "few clouds" to "🌤 약간 흐림",
            "scattered clouds" to "⛅ 흐림",
            "broken clouds" to "☁️ 흐림",
            "overcast clouds" to "🌫️ 매우 흐림",
            "shower rain" to "🌦 소나기",
            "rain" to "🌧 비",
            "moderate rain" to "🌧 비",
            "light rain" to "🌧 약한 비",
            "light intensity drizzle" to "🌧 약한 비",
            "thunderstorm" to "⛈️ 천둥·번개",
            "snow" to "❄️ 눈",
            "haze" to "🌁 안개",
            "mist" to "🌁 안개"
        )

        // 변환된 날씨 상태 가져오기 (없으면 원래 값 사용)
        val translatedWeather = weatherMap[weatherDescription] ?: weatherDescription
        tvWeather.text = "$translatedWeather  ${roundedTemp}°C"

        getKoreanLocationName(lat1, lon1)
        // 날씨에 맞는 배경 이미지 설정
        setWeatherBackground(weatherDescription)
    }

    // 날씨에 맞는 배경 이미지 설정
    private fun setWeatherBackground(weatherDescription: String) {
        val backgroundResId = when (weatherDescription) {
            "clear sky" -> R.drawable.background_sunny // 맑은 날씨 배경
            "few clouds" -> R.drawable.background_partly_cloudy // 약간 흐림
            "scattered clouds" -> R.drawable.background_cloudy // 흐림
            "broken clouds" -> R.drawable.background_cloudy // 흐림
            "overcast clouds" -> R.drawable.background_very_cloudy // 매우 흐림
            "shower rain" -> R.drawable.background_showers // 소나기
            "rain" -> R.drawable.background_rainy // 비
            "moderate rain" -> R.drawable.background_rainy // 젇당한 비
            "light rain" -> R.drawable.background_rainy // 약한 비
            "light intensity drizzle" -> R.drawable.background_rainy // 가벼운 강도의 이슿비
            "thunderstorm" -> R.drawable.background_thunderstorm // 천둥·번개
            "snow" -> R.drawable.background_snowy // 눈
            "haze" -> R.drawable.background_foggy // 안개
            "mist" -> R.drawable.background_foggy // 안개
            else -> R.drawable.background_default // 기본 배경
        }

        // CardView 배경에 이미지 설정 후 투명도 적용
        val cardView = findViewById<CardView>(R.id.grid0)
        val backgroundDrawable = ContextCompat.getDrawable(this, backgroundResId)

        // 이미지의 alpha 값 설정 (0.0f ~ 1.0f, 0은 완전히 투명, 1은 불투명)
        backgroundDrawable?.alpha = 120  // 투명도 (0-255 범위, 255가 불투명)

        cardView.background = backgroundDrawable  // CardView에 배경 설정
    }

    private fun getKoreanLocationName(lat: Double, lon: Double) {
        val geocoder = Geocoder(this, Locale.KOREAN)
        try {
            val addressList = geocoder.getFromLocation(lat, lon, 1)
            Log.d("Geocoder", "요청 위도: $lat, 경도: $lon")
            Log.d("Geocoder", "반환된 주소 리스트: $addressList")

            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val fullAddress = listOfNotNull(
                    address.locality,      // 시
                    address.subLocality,   // 구/동
                    address.thoroughfare,  // 거리명
                    address.subAdminArea   // 추가 주소
                ).joinToString(" ")

                Log.d("Geocoder", "최종 주소: $fullAddress")
                tvLocation.text = "📍 $fullAddress"
            } else {
                Log.w("Geocoder", "주소 리스트가 비어있습니다.")
                tvLocation.text = "📍 위치 정보 없음"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Geocoder", "주소 변환 중 오류 발생: ${e.message}")
            tvLocation.text = "📍 주소 변환 실패"
        }
    }

    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> { // 위치 권한 처리
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates()

                    // 👉 위치 권한이 허용된 경우, 블루투스 권한 요청
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                        ) {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(
                                    Manifest.permission.BLUETOOTH_CONNECT,
                                    Manifest.permission.BLUETOOTH_SCAN
                                ),
                                REQUEST_BLUETOOTH_PERMISSIONS
                            )
                        }
                    }
                } else {
                    Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }

            REQUEST_BLUETOOTH_PERMISSIONS -> {
                if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "블루투스 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 위치 업데이트 중지 (앱 종료 시)
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        handler.removeCallbacksAndMessages(null) // 핸들러 종료
    }
}
