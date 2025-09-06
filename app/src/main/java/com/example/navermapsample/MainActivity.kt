package com.example.navermapsample

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import com.example.navermapsample.databinding.ActivityMainBinding
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import android.widget.EditText
import com.naver.maps.map.CameraUpdate
import android.content.Intent
import android.content.Context
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import android.view.View
import com.naver.maps.map.overlay.PolylineOverlay
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header
import android.widget.ImageButton
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.location.LastLocationRequest
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1004
        private val PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private val markers = ArrayList<Marker>()
    private var currentPolyline: PolylineOverlay = PolylineOverlay()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle


    private val locationsMap = mapOf(
        "공학2관" to LatLng(36.9070836, 127.1421894),
        "공학1관" to LatLng(36.9073555, 127.1439416),
        "화정관" to LatLng(36.9081418, 127.1415356),
        "상경학관" to LatLng(36.9084404, 127.1440260),
        "도서관" to LatLng(36.9091203, 127.1434068),
        "학생복지회관" to LatLng(36.9100776, 127.1435208),
        "본관" to LatLng(36.907069, 127.1430480),
        "보건의료학관" to LatLng(36.9082391, 127.1451439),
        "조형학관" to LatLng(36.9087361, 127.1449541),
        "인문사회회관" to LatLng(36.9090157, 127.1448907),
        "21세기개발관" to LatLng(36.9099996, 127.1447392),
        "지식정보관" to LatLng(36.9104416, 127.1448391),
        "디자인정보관" to LatLng(36.9095069, 127.1455334)
    )

    // 필터 데이터: 이름과 좌표
    private val cafeLocations = listOf(
        "그라찌에" to LatLng(36.9075, 127.1430),
        "브레덴콕" to LatLng(36.9080, 127.1440),
        "ing" to LatLng(36.9089,127.1400)
    )

    private val restroomLocations = listOf(
        "Restroom A" to LatLng(36.9065, 127.1420),
        "Restroom B" to LatLng(36.9095, 127.1450)
    )

    private val smokingAreaLocations = listOf(
        "Smoking Area A" to LatLng(36.9078, 127.1435),
        "Smoking Area B" to LatLng(36.9082, 127.1445)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 드로어 레이아웃 및 툴바 초기화
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // ActionBarDrawerToggle 설정
        val drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        // 토글 버튼 아이콘 색상 설정
        drawerToggle.drawerArrowDrawable.color = Color.WHITE

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // 네비게이션뷰의 아이템 클릭 리스너 설정
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        if (!hasPermission()) {
            locationPermissionRequest.launch(PERMISSIONS)
        } else {
            initMapView()
        }

        // 검색창(AutoCompleteTextView) 찾기
        val searchAutoCompleteTextView: AutoCompleteTextView =
            findViewById(R.id.autoCompleteTextView)

        // AutoCompleteTextView에 어댑터 설정
        val locations = locationsMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        searchAutoCompleteTextView.setAdapter(adapter)
        searchAutoCompleteTextView.threshold = 1

        // 검색창에 리스너 설정
        searchAutoCompleteTextView.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                true // 이벤트 처리 완료
            } else {
                false // 다른 키 이벤트에 대해서는 처리하지 않음
            }
        }

        // 검색 버튼에 클릭 리스너 설정
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            performSearch(searchAutoCompleteTextView.text.toString())
        }

        // 마커와 경로를 삭제하는 삭제 버튼 리스너
        val resetMarkersButton: ImageButton = findViewById(R.id.resetMarkersButton)
        resetMarkersButton.setOnClickListener {
            clearPreviousMarkerAndRoute()
            Toast.makeText(this, "검색결과가 삭제되었습니다", Toast.LENGTH_SHORT).show()
        }

        val filterCafeButton: Button = findViewById(R.id.filterCafe)
        val filterRestroomButton: Button = findViewById(R.id.filterRestroom)
        val filterSmokingButton: Button = findViewById(R.id.filterSmoking)

        filterCafeButton.setOnClickListener {
            updateMarkers(cafeLocations, "카페")
        }

        filterRestroomButton.setOnClickListener {
            updateMarkers(restroomLocations, "화장실")
        }

        filterSmokingButton.setOnClickListener {
            updateMarkers(smokingAreaLocations, "흡연장")
        }
    }

    // ... existing methods ...

    // 검색 기능을 수행하는 메소드
    private fun performSearch(searchText: String) {
        if (searchText.isBlank()) {
            Toast.makeText(this, "장소를 입력하세요", Toast.LENGTH_SHORT).show()
        } else {
            val location = locationsMap[searchText]
            if (location != null) {
                clearPreviousMarkerAndRoute()
                addMarker(location, searchText)
            } else {
                Toast.makeText(this, "검색 결과 없음", Toast.LENGTH_SHORT).show()
            }
        }
        currentFocus?.let { view ->
            hideKeyboard(view)
        }
    }

    // 이전 마커 및 경로를 지우는 함수
    private fun clearPreviousMarkerAndRoute() {
        markers.forEach { it.map = null }
        currentPolyline.map = null
        markers.clear()
        currentMarker?.map = null
        currentMarker = null
    }

    // 키보드를 숨기는 함수
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // 위치 관련 메소드
    private fun initMapView() {
        binding.stationMap.getMapAsync { naverMap ->
            this.naverMap = naverMap
            locationSource =
                FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
            naverMap.locationSource = locationSource
            naverMap.uiSettings.isLocationButtonEnabled = true
            naverMap.locationTrackingMode = LocationTrackingMode.Follow

            locationSource.lastLocation?.let {
                val currentLocation = LatLng(it.latitude, it.longitude)
                naverMap.moveCamera(CameraUpdate.scrollTo(currentLocation))
                naverMap.moveCamera(CameraUpdate.zoomTo(40.0))
            }
        }
    }

    private fun hasPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(this, PERMISSIONS[0]) ==
                PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, PERMISSIONS[1]) ==
                PermissionChecker.PERMISSION_GRANTED
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                initMapView()
            }

            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                initMapView()
            }

            else -> {
                Toast.makeText(this, "권한 없음", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermission()) {
            locationPermissionRequest.launch(PERMISSIONS)
        } else {
            initMapView()
        }
    }

    private var currentMarker: Marker? = null

    private fun addMarker(location: LatLng, title: String) {
        clearPreviousMarkerAndRoute()
        val marker = Marker().apply {
            position = location
            captionText = title
            map = naverMap
        }
        currentMarker = marker
        naverMap.moveCamera(CameraUpdate.scrollTo(location))
        naverMap.moveCamera(CameraUpdate.zoomTo(17.0))
        getRouteFromCurrentLocationToMarker(location)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the home action
                val intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            }

            R.id.nav_profile -> {
                // Handle the profile action
                Toast.makeText(
                    this, "현재 실내에서 강의실 찾기는 개발중입니다\n\n" +
                            "추후 공개될 예정입니다", Toast.LENGTH_SHORT
                ).show()
            }

            R.id.nav_help -> {
                val intent = Intent(this, Help::class.java)
                startActivity(intent)
            }
        }
        // Close the navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getRouteFromCurrentLocationToMarker(markerLocation: LatLng) {
        val currentLocation = locationSource.lastLocation
        if (currentLocation != null) {
            val start = "${currentLocation.longitude},${currentLocation.latitude}"
            val goal = "${markerLocation.longitude},${markerLocation.latitude}"
            val retrofit = Retrofit.Builder()
                .baseUrl("https://naveropenapi.apigw.ntruss.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(NaverDirectionsApi::class.java)
            val call = service.getDrivingRoute(
                start, goal,
                clientId = "fv6ftsph28",
                clientSecret = "n3G5R7NRT1r8UkbffCZOiDvgxZQI9zAApKwYBYTB"
            )
            call.enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(
                    call: Call<DirectionsResponse>,
                    response: Response<DirectionsResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.route?.trafast?.get(0)?.path?.let { path ->
                            displayRoute(path)
                        } ?: run {
                            Toast.makeText(this@MainActivity, "경로를 찾지 못하였습니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "경로를 찾지 못하였습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } else {
            Toast.makeText(this, "Current location not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayRoute(path: List<List<Double>>) {
        currentPolyline.map = null
        val polylineOverlay = PolylineOverlay().apply {
            coords = path.map { LatLng(it[1], it[0]) }.toMutableList()
            color = Color.RED
            width = 10
        }
        polylineOverlay.map = naverMap
        currentPolyline = polylineOverlay
    }

    // 마커를 업데이트할 때 클릭 리스너 추가
    private fun updateMarkers(locations: List<Pair<String, LatLng>>, category: String) {
        // 기존 마커 삭제
        markers.forEach { it.map = null }
        markers.clear()

        // 새 마커 추가
        locations.forEach { (name, location) ->
            val marker = Marker().apply {
                position = location
                captionText = name // 마커에 이름 표시
                map = naverMap
                // 마커 클릭 리스너 설정
                setOnClickListener {
                    showMarkerInfoDialog(name, location)
                    true  // 클릭 이벤트 처리 완료
                }
            }
            markers.add(marker)
        }

        // 지도 카메라 이동 (첫 번째 마커 기준)
        if (locations.isNotEmpty()) {
            naverMap.moveCamera(CameraUpdate.scrollTo(locations[0].second))
            naverMap.moveCamera(CameraUpdate.zoomTo(16.0))
        }

        Toast.makeText(this, "$category 필터 적용", Toast.LENGTH_SHORT).show()
    }

    // 마커 클릭 시 정보 표시용 다이얼로그 생성
    private fun showMarkerInfoDialog(name: String, location: LatLng) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("장소 정보")
            .setMessage("장소 이름: $name\n위도: ${location.latitude}\n경도: ${location.longitude}")
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

}

interface NaverDirectionsApi {
    @GET("map-direction/v1/driving")
    fun getDrivingRoute(
        @Query("start") start: String,
        @Query("goal") goal: String,
        @Query("option") option: String = "trafast",
        @Header("X-NCP-APIGW-API-KEY-ID") clientId: String,
        @Header("X-NCP-APIGW-API-KEY") clientSecret: String
    ): Call<DirectionsResponse>
}

data class DirectionsResponse(
    val code: Int,
    val message: String,
    val currentDateTime: String,
    val route: Route
)

data class Route(
    val trafast: List<TraFast>
)

data class TraFast(
    val summary: Summary,
    val path: List<List<Double>>
)

data class Summary(
    val start: Point,
    val goal: Point,
    val distance: Double,
    val duration: Double
)

data class Point(
    val location: List<Double>
)
