// ✅ 새롭게 정리된 LocationManager (JSON 사용 기반)
package com.example.navermapsample

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.naver.maps.geometry.LatLng
import java.io.InputStreamReader

object LocationManager {
    lateinit var locationsMap: Map<String, LatLng> // 검색용 데이터
    private val filterLocations: MutableMap<String, List<Pair<String, LatLng>>> = mutableMapOf() // 필터용 데이터

    // JSON 로딩 함수
    fun loadLocations(context: Context) {
        val gson = Gson()

        // 🔹 locations.json 로드 (검색용)
        val locationsInput = context.assets.open("locations.json")
        val locationsType = object : TypeToken<Map<String, Building>>() {}.type
        val locationsData: Map<String, Building> = gson.fromJson(InputStreamReader(locationsInput), locationsType)

        val mutableLocationsMap = mutableMapOf<String, LatLng>()
        for ((buildingName, building) in locationsData) {
            val latLng = LatLng(building.latitude, building.longitude)

            // 건물 이름 등록
            mutableLocationsMap[buildingName] = latLng

            // 강의실 이름 등록 (예: "E201" → 해당 건물의 위치)
            building.rooms?.forEach { room ->
                mutableLocationsMap[room] = latLng
            }
        }
        locationsMap = mutableLocationsMap

        // 🔹 filters.json 로드 (필터용)
        val filtersInput = context.assets.open("filters.json")
        val filtersType = object : TypeToken<Map<String, Map<String, Location>>>() {}.type
        val filtersData: Map<String, Map<String, Location>> = gson.fromJson(InputStreamReader(filtersInput), filtersType)

        for ((filterType, items) in filtersData) {
            val locations = items.map { it.key to LatLng(it.value.latitude, it.value.longitude) }
            filterLocations[filterType] = locations
        }
    }


    // 필터 데이터 가져오기
    fun getFilteredLocations(filterType: String): List<Pair<String, LatLng>> {
        return filterLocations[filterType] ?: emptyList()
    }

    // 검색용 데이터 클래스
    data class Location(
        val latitude: Double,
        val longitude: Double
    )
    data class Building(
        val latitude: Double,
        val longitude: Double,
        val rooms: List<String>? = null
    )

}
