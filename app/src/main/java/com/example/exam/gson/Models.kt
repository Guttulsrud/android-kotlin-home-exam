package com.example.exam.gson

data class Locations(
    val features: List<Location>
    ) {

    data class Location(
        var properties: Properties?,
        var geometry: Geometry?,
        val name: String?,
        val icon: String?,
        val apiId: Long?,
        val longitude: Double?,
        val latitude: Double?
    )
}

data class Location(
    var id: Long?,
    val name: String?,
    val icon: String?,
    val apiId: Long?,
    val longitude: Double?,
    val latitude: Double?
)

data class LocationDetails(
    val place: PlaceDetails
)

data class PlaceDetails(
    val comments: String,
    val banner: String
)

data class Properties(
    var name: String?,
    var icon: String?,
    var id: Long?
)

data class Geometry(
    var type: String?,
    var coordinates: List<Double>?
)