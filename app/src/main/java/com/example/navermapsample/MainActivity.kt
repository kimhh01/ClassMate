package com.example.navermapsample

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
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
import com.naver.maps.map.CameraUpdate
import android.content.Intent
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.text.Editable
import android.text.TextWatcher
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
import android.view.MotionEvent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.example.navermapsample.LocationManager.locationsMap
import com.google.android.gms.maps.model.Polyline
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.File
import kotlin.jvm.java



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1004
        private val PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
        // 클래스 멤버 변수로 선언된 AutoCompleteTextView
        private lateinit var searchAutoCompleteTextView: AutoCompleteTextView // <--- 이 변수도 lateinit으로 선언되어 있습니다.

        private lateinit var findClassroomButton: Button // lateinit으로 선언됨
    }
    private val EDIT_PROFILE_REQUEST_CODE = 1001


    private lateinit var binding: ActivityMainBinding
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val markers = ArrayList<Marker>()
    private var currentPolyline: PolylineOverlay = PolylineOverlay()
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var resetMarkersButton: ImageButton  // 전역 변수로 선언
    private var currentRoute: Polyline? = null
    private var remainingPolyline = PolylineOverlay()
    private var passedPolyline = PolylineOverlay()
    private var isFollowingUser = true
    private var isTimerRunning = false          // 타이머 중복 실행 방지
    private var userGestureDetected = false     // 사용자 제스처 감지 여부
    lateinit var headerView: View
    private val handler = Handler(Looper.getMainLooper())
    private var routeCoords: MutableList<LatLng> = mutableListOf()
    private var isNavigationActive = false
    private lateinit var findClassroomButton: Button
    private val DISTANCE_THRESHOLD = 50.0 // 50 meters threshold
    private var destinationLocation: LatLng? = null





    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔹 JSON 파일 로드
        LocationManager.loadLocations(this)

        // Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // resetMarkersButton 초기화
        resetMarkersButton = findViewById(R.id.resetMarkersButton)

        // 드로어 레이아웃 초기화
        drawerLayout = findViewById(R.id.drawer_layout)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        findClassroomButton = findViewById(R.id.findClassroomButton)

        searchAutoCompleteTextView = findViewById(R.id.autoCompleteTextView)

        // 툴바 초기화 (툴바는 이제 사용하지 않음)
        // val toolbar: Toolbar = findViewById(R.id.toolbar)
        // setSupportActionBar(toolbar)

        // ActionBarDrawerToggle 설정 (기본 제공 기능 사용)
        val drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, null,  // 툴바를 null로 설정
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        // 강의실 찾기 버튼 클릭 리스너 부분
        findClassroomButton.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)

           // val searchAutoCompleteTextView: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
            // 강의실 번호를 포함한 검색어 추출하기
            // searchAutoCompleteTextView (클래스 멤버 변수)를 바로 사용합니다.
            val searchText = searchAutoCompleteTextView.text.toString() // <--- 멤버 변수 사용

            var roomNumber = ""

            // 검색어에서 숫자 부분만 추출
            val regex = "\\d+".toRegex()
            val matchResult = regex.find(searchText)
            if (matchResult != null) {
                roomNumber = matchResult.value
            }
            Log.d("MainActivity", "추출된 강의실 번호: $roomNumber")

            // 건물 좌표 및 추출한 강의실 번호 전달
            destinationLocation?.let { location ->
                intent.putExtra("room_number", roomNumber) // 강의실 번호만 전달
                Log.d("MainActivity", "강의실 번호 전달: $roomNumber")
            } ?: run {
                Log.w("MainActivity", "강의실 번호를 전달하지 못했습니다.")
            }

            startActivity(intent)
        }

        // 드로어 토글 기능 활성화
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // 네비게이션뷰의 아이템 클릭 리스너 설정
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // 헤더를 직접 inflate (중복 방지 필수!)
        if (binding.navView.headerCount == 0) {
            headerView = binding.navView.inflateHeaderView(R.layout.nav_header)
        } else {
            headerView = binding.navView.getHeaderView(0)
        }

        setupDrawerHeader()

        // 위치 권한 체크
        if (!hasPermission()) {
            locationPermissionRequest.launch(PERMISSIONS)
        } else {
            initMapView()
        }

        // 검색창(AutoCompleteTextView) 찾기
        val searchAutoCompleteTextView: AutoCompleteTextView =
            findViewById(R.id.autoCompleteTextView)
        searchAutoCompleteTextView.setDropDownBackgroundResource(R.drawable.rounded_dropdown_background)
        searchAutoCompleteTextView.dropDownVerticalOffset = 20  // dp 단위로 내려줌

        // 툴바의 드로어 토글 아이콘을 검색창 내부로 이동시키기
        val drawerToggleIcon = drawerToggle.drawerArrowDrawable
        searchAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(
            drawerToggleIcon, null, null, null
        )

        // 검색창에 드로어 아이콘 클릭 시 드로어 열기/닫기 처리
        searchAutoCompleteTextView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.x < (searchAutoCompleteTextView.compoundDrawables[0]?.bounds?.right ?: 0)) {
                    // 드로어 아이콘 영역 클릭 시 드로어 열기/닫기 처리
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                    // 키보드 내리기
                    hideKeyboard(v)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        // 검색창 어댑터 설정
        val locations = locationsMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, locations)
        searchAutoCompleteTextView.setAdapter(adapter)
        searchAutoCompleteTextView.threshold = 1

        val touchInterceptor = findViewById<View>(R.id.touchInterceptor)
        // 포커스에 따라 힌트 변경
        searchAutoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchAutoCompleteTextView.hint = "강의실 번호나 건물 이름을 입력하세요" // 포커스 시 힌트
                touchInterceptor.visibility = View.VISIBLE
            } else {
                searchAutoCompleteTextView.hint = "어디로 가세요?" // 포커스 해제 시 힌트
                touchInterceptor.visibility = View.GONE
            }
        }

        // 검색창에 리스너 설정
        searchAutoCompleteTextView.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v.text.toString())
                true
            } else {
                false
            }
        }

        searchAutoCompleteTextView.setOnFocusChangeListener { _, hasFocus ->
            touchInterceptor.visibility = if (hasFocus) View.VISIBLE else View.GONE
        }

        touchInterceptor.setOnTouchListener { _, _ ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchAutoCompleteTextView.windowToken, 0)
            searchAutoCompleteTextView.clearFocus()
            touchInterceptor.visibility = View.GONE
            true
        }

        // 검색 버튼 클릭 리스너 설정
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        searchButton.setOnClickListener {
            performSearch(searchAutoCompleteTextView.text.toString())
        }

        // UI 요소 가져오기
        val resetMarkersButton: ImageButton = findViewById(R.id.resetMarkersButton)
        val filterCafeButton: Button = findViewById(R.id.filterCafe)
        val filterStoreButton: Button = findViewById(R.id.filterStore)
        val filterSmokingButton: Button = findViewById(R.id.filterSmoking)
        val filterATMButton: Button = findViewById(R.id.filterATM)

        // 🔹 삭제 버튼 초기 숨김 처리
        resetMarkersButton.visibility = View.GONE

        // 🔹 검색창 입력 감지 (입력 시 삭제 버튼 표시, 입력 없으면 숨김)
        searchAutoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                resetMarkersButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 🔹 필터 버튼 클릭 리스너
        val filterButtons = listOf(filterCafeButton, filterStoreButton, filterSmokingButton, filterATMButton)
        filterButtons.forEach { button ->
            button.setOnClickListener {
                resetMarkersButton.visibility = View.VISIBLE
            }
        }

        // 🔹 삭제 버튼 클릭 리스너
        resetMarkersButton.setOnClickListener {
            clearPreviousMarkerAndRoute()
            searchAutoCompleteTextView.text.clear()
            resetMarkersButton.visibility = View.GONE
            Toast.makeText(this, "검색결과가 삭제되었습니다", Toast.LENGTH_SHORT).show()
        }

        // 필터 버튼 클릭 리스너
        filterCafeButton.setOnClickListener {
            // 기존 경로 삭제
            clearPreviousMarkerAndRoute()
            //삭제버튼 상태
            resetMarkersButton.visibility = View.VISIBLE
            updateMarkers(LocationManager.getFilteredLocations("cafe"), "카페")
        }

        filterStoreButton.setOnClickListener {
            // 기존 경로 삭제
            clearPreviousMarkerAndRoute()
            //삭제버튼 상태
            resetMarkersButton.visibility = View.VISIBLE
            updateMarkers(LocationManager.getFilteredLocations("store"), "편의점")
        }

        filterSmokingButton.setOnClickListener {
            // 기존 경로 삭제
            clearPreviousMarkerAndRoute()
            //삭제버튼 상태
            resetMarkersButton.visibility = View.VISIBLE
            updateMarkers(LocationManager.getFilteredLocations("smoking"), "흡연장")
        }

        filterATMButton.setOnClickListener {
            // 기존 경로 삭제
            clearPreviousMarkerAndRoute()
            //삭제버튼 상태
            resetMarkersButton.visibility = View.VISIBLE
            updateMarkers(LocationManager.getFilteredLocations("atm"), "ATM")
        }

    }
    fun setupDrawerHeader() {
        val nicknameTextView = headerView.findViewById<TextView>(R.id.nicknameTextView)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profileImageView)
        //val editProfileButton = headerView.findViewById<ImageButton>(R.id.editProfileButton)

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val nickname = prefs.getString("nickname", "닉네임")
        nicknameTextView.text = nickname ?: "닉네임"

        // 📌 내부 저장소에서 profile.jpg 불러오기
        val file = File(filesDir, "profile.jpg")
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            profileImageView.setImageBitmap(bitmap)
        } else {
            Log.d("DrawerHeader", "profile.jpg 파일이 존재하지 않음")
        }

        // 프로필 수정 화면으로 이동
        //editProfileButton.setOnClickListener {
            //Log.d("Drawer", "수정 버튼 클릭됨")
            //startActivity(Intent(this, EditProfileActivity::class.java))
        //}
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setupDrawerHeader()  // 닉네임/이미지를 다시 읽어와서 갱신
        }
    }


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
        // 마커 제거
        markers.forEach { it.map = null }
        markers.clear()

        // 현재 선택된 마커 제거
        currentMarker?.map = null
        currentMarker = null

        // 경로(Polyline) 제거
        currentPolyline.map = null
        passedPolyline.map = null
        remainingPolyline.map = null
        routeCoords.clear()
        stopNavigation()
        isFollowingUser = true

        // 자동 이동 가능 상태로 전환
        userGestureDetected = true
        isTimerRunning = false

        Log.d("MapTracking", "🧹 지도 요소 전부 제거됨 - 자동 이동 가능 상태로 변경됨")

        destinationLocation = null
        findClassroomButton.visibility = View.GONE
        isClassroomButtonShown = false
    }

    // 키보드를 숨기는 함수
    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun initMapView() {
        binding.stationMap.getMapAsync { naverMap ->
            this.naverMap = naverMap

            // 📍 위치 설정
            locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
            naverMap.locationSource = locationSource
            naverMap.uiSettings.isLocationButtonEnabled = true
            naverMap.locationOverlay.isVisible = true
            naverMap.locationTrackingMode = LocationTrackingMode.Follow

            // 📌 카메라 자동 복귀용 핸들러 및 플래그
            val handler = Handler(Looper.getMainLooper())
            var isTimerRunning = false

            val returnToCurrentLocation = Runnable {
                if (markers.isEmpty()) {
                    val location = locationSource.lastLocation
                    if (location != null) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val update = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Fly)
                        naverMap.moveCamera(update)
                        Toast.makeText(this, "현재 위치로 자동 이동했습니다", Toast.LENGTH_SHORT).show()
                        Log.d("MapTracking", "📍 사용자 조작 후 자동 복귀 실행")
                    } else {
                        Log.d("MapTracking", "⚠️ 현재 위치 정보를 가져올 수 없음")
                    }
                } else {
                    Log.d("MapTracking", "🚫 마커 존재 - 자동 이동 생략")
                }
                isTimerRunning = false
            }

            // 🔹 초기 카메라 위치 설정
            locationSource.lastLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                val update = CameraUpdate.toCameraPosition(CameraPosition(latLng, 19.5)).animate(CameraAnimation.Fly)
                naverMap.moveCamera(update)
            }

            // 🔹 사용자 카메라 조작 감지
            naverMap.addOnCameraChangeListener { reason: Int, _ ->
                if (reason == -1) {
                    Log.d("MapTracking", "👆 사용자 제스처 감지됨!")

                    if (!isTimerRunning) {
                        Log.d("MapTracking", "⏳ 타이머 시작 (10초 후 복귀)")
                        handler.postDelayed(returnToCurrentLocation, 10000)
                        isTimerRunning = true
                    } else {
                        Log.d("MapTracking", "🚫 이미 타이머 실행 중 - 중복 실행 방지")
                    }
                }
            }

            // 🔍 Intent로부터 강의실 이름을 받아 자동 검색
            val classroomName = intent.getStringExtra("classroom")
            if (!classroomName.isNullOrEmpty()) {
                Log.d("IntentSearch", "🎯 강의실 이름 전달됨: $classroomName")

                val searchAutoCompleteTextView: AutoCompleteTextView = findViewById(R.id.autoCompleteTextView)
                val searchButton: ImageButton = findViewById(R.id.searchButton)

                // 강의실 이름을 AutoCompleteTextView에 설정
                searchAutoCompleteTextView.setText(classroomName)

                // 다이얼로그 생성
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("해당 강의실로 경로를 찾는중 입니다...")
                progressDialog.setCancelable(false) // 사용자가 다이얼로그를 닫을 수 없게 설정
                progressDialog.show()

                // 7초 후 자동 검색 실행
                handler.postDelayed({
                    searchButton.performClick()
                    Log.d("IntentSearch", "🔎 검색 버튼 클릭 실행됨")

                    // 검색이 완료되면 다이얼로그 종료
                    progressDialog.dismiss()
                }, 7000) // 원하는 시간(7000ms)에 맞춰 동작
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
        setupDrawerHeader()

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
        destinationLocation = location // Store destination location
        naverMap.moveCamera(CameraUpdate.scrollTo(location))
        naverMap.moveCamera(CameraUpdate.zoomTo(17.0))
        getRouteFromCurrentLocationToMarker(location)

        // Always hide the button when a new location is searched
        findClassroomButton.visibility = View.GONE
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.edit_profile -> {
                // Handle the home action
                val intent = Intent(this, EditProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.qr_class -> {
                // Handle the profile action
                val intent = Intent(this, QrCodeScanActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_help -> {
                val intent = Intent(this, SupportActivity::class.java)
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
                clientId = BuildConfig.NAVER_CLIENT_ID,
                clientSecret = BuildConfig.NAVER_CLIENT_SECRET
            )


            call.enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.route?.trafast?.get(0)?.path?.let { path ->
                            routeCoords = path.map { LatLng(it[1], it[0]) }.toMutableList()
                            displayRoute(routeCoords, currentLocation)
                            startNavigation() // 검색 후 네비게이션 자동 시작
                            clearOtherMarkers(markerLocation)
                            resetMarkersButton.visibility = View.VISIBLE
                        } ?: Toast.makeText(this@MainActivity, "경로를 찾지 못하였습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "경로를 찾지 못하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "현재 위치를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    // 경로가 표시된 후 다른 마커들을 지우는 함수
    private fun clearOtherMarkers(exceptMarker: LatLng) {
        markers.filter { it.position != exceptMarker }.forEach { it.map = null }
        markers.removeAll { it.position != exceptMarker }
    }

    // 경로 표시
    private fun displayRoute(coords: List<LatLng>, currentLocation: Location) {
        if (coords.size < 2) {
            Toast.makeText(this, "경로가 너무 짧습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 전체 경로 표시
        currentPolyline.map = null
        currentPolyline.coords = coords
        currentPolyline.color = Color.BLUE
        currentPolyline.width = 10
        currentPolyline.map = naverMap

        // 지나온 경로와 남은 경로 업데이트
        updateRouteBasedOnLocation(currentLocation)

        // 카메라 조정
        val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val targetLatLng = coords.last()
        val bounds = LatLngBounds.Builder()
            .include(currentLatLng)
            .include(targetLatLng)
            .build()
        val cameraUpdate = CameraUpdate.fitBounds(bounds, 100).animate(CameraAnimation.Fly)
        naverMap.moveCamera(cameraUpdate)
    }

    // 위치 기반 경로 업데이트
    private fun updateRouteBasedOnLocation(currentLocation: Location) {
        if (routeCoords.size < 2) return

        // 가장 가까운 경로 지점 찾기
        val nearestIndex = routeCoords.indices.minByOrNull { i ->
            distanceBetween(currentLocation, routeCoords[i])
        } ?: 0

        // 지나온 경로
        passedPolyline.map = null
        if (nearestIndex >= 1) {
            val passedCoords = routeCoords.subList(0, nearestIndex + 1)
            passedPolyline.coords = passedCoords
            passedPolyline.color = Color.GRAY
            passedPolyline.width = 10
            passedPolyline.map = naverMap
        }

        // 남은 경로
        remainingPolyline.map = null
        if (nearestIndex < routeCoords.size - 1) {
            val remainingCoords = routeCoords.subList(nearestIndex, routeCoords.size)
            remainingPolyline.coords = remainingCoords
            remainingPolyline.color = Color.parseColor("#046DDD")
            remainingPolyline.width = 10
            remainingPolyline.map = naverMap
        }
    }

    @SuppressLint("MissingPermission")
    private fun startNavigation() {
        if (isNavigationActive) return

        if (!hasPermission()) {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        isNavigationActive = true
        isFollowingUser = true

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }


    // 네비게이션 종료
    private fun stopNavigation() {
        if (!isNavigationActive) return
        isNavigationActive = false
        fusedLocationClient.removeLocationUpdates(locationCallback)
        passedPolyline.map = null
        remainingPolyline.map = null
    }

    // 위치 업데이트 콜백
    // Modify the locationCallback to check distance to destination
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                if (isNavigationActive) {
                    updateRouteBasedOnLocation(location)

                    // Check distance to destination
                    destinationLocation?.let { dest ->
                        val distanceToDestination = calculateDistance(
                            location.latitude, location.longitude,
                            dest.latitude, dest.longitude
                        )

                        // Show or hide button based on distance
                        runOnUiThread {
                            if (distanceToDestination <= DISTANCE_THRESHOLD) {
                                findClassroomButton.visibility = View.VISIBLE
                                if (!isClassroomButtonShown) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "건물에 도착했습니다. 강의실 찾기를 이용하세요.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isClassroomButtonShown = true
                                }
                            } else {
                                findClassroomButton.visibility = View.GONE
                                isClassroomButtonShown = false
                            }
                        }
                    }

                    if (isFollowingUser) {
                        val latLng = LatLng(location.latitude, location.longitude)
                        val update = CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Fly)
                        naverMap.moveCamera(update)
                    }
                }
            }
        }
    }

    // Add a flag to prevent repeated toast messages
    private var isClassroomButtonShown = false

    // Add this function to calculate distance using Haversine formula
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0].toDouble()
    }

    // 거리 계산
    private fun distanceBetween(p1: Location, p2: LatLng): Double {
        val results = FloatArray(1)
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results)
        return results[0].toDouble()
    }

    // 마커 업데이트
    private fun updateMarkers(locations: List<Pair<String, LatLng>>, category: String) {
        markers.forEach { it.map = null }
        markers.clear()

        locations.forEach { (name, location) ->
            val marker = Marker().apply {
                position = location
                captionText = name
                map = naverMap
                setOnClickListener {
                    showMarkerInfoBottomSheet(name, location, category)
                    true
                }
            }
            markers.add(marker)
        }

        if (locations.isNotEmpty()) {
            naverMap.moveCamera(CameraUpdate.scrollTo(locations[0].second))
            naverMap.moveCamera(CameraUpdate.zoomTo(16.0))
        }

        Toast.makeText(this, "$category 필터 적용", Toast.LENGTH_SHORT).show()
    }

    // 바텀시트 표시
    private fun showMarkerInfoBottomSheet(name: String, location: LatLng, category: String) {
        val bottomSheet = CustomBottomSheetDialog(name, location, category) { selectedLocation ->
            getRouteFromCurrentLocationToMarker(selectedLocation)
        }
        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
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