package com.example.navermapsample

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.geometry.LatLng
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.Locale

class CustomBottomSheetDialog(
    private val name: String,
    private val location: LatLng,
    private val category: String,
    private val onNavigateClick: ((LatLng) -> Unit)? = null

) : BottomSheetDialogFragment() {

    private lateinit var nameTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var navigateButton: Button
    private lateinit var placeImageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        nameTextView = view.findViewById(R.id.nameTextView)
        locationTextView = view.findViewById(R.id.locationTextView)
        phoneTextView = view.findViewById(R.id.telephoneTextView)
        navigateButton = view.findViewById(R.id.navigateButton)
        placeImageView = view.findViewById(R.id.placeImageView)

        placeImageView.visibility = View.GONE

        nameTextView.text = name
        locationTextView.text = getAddressFromLocation(requireContext(), location.latitude, location.longitude)

        // ✅ 여기서 fetchBuildingInfo & fetchBuildingImage 실행
        if (category == "카페" || category == "편의점") {
            fetchBuildingInfo(name)
            fetchBuildingImage(name)
        } // 추가된 이미지 검색 함수 호출

        navigateButton.setOnClickListener {
            onNavigateClick?.invoke(location)
            dismiss()
        }

        view.post {
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                behavior.peekHeight = 400
                behavior.isFitToContents = false
                behavior.expandedOffset = 100
                it.setBackgroundResource(R.drawable.bottom_sheet_background)
                it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

                behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {}

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        if (slideOffset > 0.3) {
                            placeImageView.visibility = View.VISIBLE
                        } else {
                            placeImageView.visibility = View.GONE
                        }
                    }
                })
            }
        }
    }

    private fun fetchBuildingInfo(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NaverSearchApi::class.java)
        val call = service.searchPlaces(
            query = query,
            clientId = BuildConfig.NAVER_SEARCH_CLIENT_ID,
            clientSecret = BuildConfig.NAVER_SEARCH_CLIENT_SECRET
        )

        call.enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                if (response.isSuccessful) {
                    val places = response.body()?.items ?: emptyList()
                    val closestPlace = places.minByOrNull { place ->
                        val placeLatLng = getLatLngFromAddress(place.address)
                        calculateDistance(location.latitude, location.longitude, placeLatLng.latitude, placeLatLng.longitude)
                    }

                    if (closestPlace != null) {
                        nameTextView.text = Html.fromHtml(closestPlace.title, Html.FROM_HTML_MODE_LEGACY).toString()
                        locationTextView.text = closestPlace.address
                        phoneTextView.text = closestPlace.telephone ?: "전화번호 정보 없음"
                    } else {
                        locationTextView.text = "해당 건물에 대한 정보는 찾을 수 없습니다."
                        phoneTextView.text = "전화번호 정보 없음"
                    }
                } else {
                    locationTextView.text = "API 응답 오류"
                    phoneTextView.text = "전화번호 정보 없음"
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                locationTextView.text = "네트워크 오류"
                phoneTextView.text = "전화번호 정보 없음"
                Toast.makeText(requireContext(), "오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            // 명시적으로 타입을 List<Address>로 선언
            val addresses: List<android.location.Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0) ?: "주소를 찾을 수 없습니다"
        } catch (e: Exception) {
            "주소 변환 오류"
        }
    }

    private fun getLatLngFromAddress(address: String): LatLng {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return try {
            val addresses: List<android.location.Address>? = geocoder.getFromLocationName(address, 1)
            if (addresses?.isNotEmpty() == true) {
                // 첫 번째 주소에서 위도, 경도를 가져와 LatLng 반환
                LatLng(addresses[0].latitude, addresses[0].longitude)
            } else {
                // 주소를 찾을 수 없을 경우 기본 좌표 반환
                LatLng(0.0, 0.0)
            }
        } catch (e: Exception) {
            // 예외 발생 시 기본 좌표 반환
            LatLng(0.0, 0.0)
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371  // 지구 반지름 (단위: 킬로미터)

        // 위도, 경도 차이
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        // Haversine 공식
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        // 지구 반지름과 c 값을 곱해서 거리 계산 (단위: 킬로미터)
        return earthRadius * c
    }



    private fun fetchBuildingImage(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.naver.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NaverSearchApi::class.java)
        val call = service.searchImages(
            query = query,
            clientId = "nWbheb0aGf6BK0qn1DMK",
            clientSecret = "OkIIxBqeOD"
        )

        call.enqueue(object : Callback<ImageSearchResponse> {
            override fun onResponse(call: Call<ImageSearchResponse>, response: Response<ImageSearchResponse>) {
                if (response.isSuccessful) {
                    val images = response.body()?.items ?: emptyList()
                    val firstImage = images.firstOrNull()?.link
                    firstImage?.let {
                        Glide.with(requireContext()).load(it).into(placeImageView)
                    }
                }
            }

            override fun onFailure(call: Call<ImageSearchResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "이미지 로드 오류: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

interface NaverSearchApi {
    @GET("v1/search/local.json")
    fun searchPlaces(
        @Query("query") query: String,
        @Query("display") display: Int = 20,
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String
    ): Call<SearchResponse>

    @GET("v1/search/image.json")
    fun searchImages(
        @Query("query") query: String,
        @Query("display") display: Int = 100,
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String
    ): Call<ImageSearchResponse>
}

data class SearchResponse(val items: List<PlaceInfo>)
data class PlaceInfo(val title: String, val address: String, val telephone: String?)
data class ImageSearchResponse(val items: List<ImageInfo>)
data class ImageInfo(val link: String)
