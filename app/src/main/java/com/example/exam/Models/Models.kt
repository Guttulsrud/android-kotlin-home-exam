package com.example.exam.Models

data class Locations(
    val features: List<Location>
) {

    data class Location(
        var properties: Properties,
        var geometry: Geometry
    )

    data class Properties(
        var name: String?,
        var icon: String?,
        var id: Long?
    )

    data class Geometry(
        var type: String?,
        var coordinates: List<Double>
    )

}

data class Location(
    var id: Long?,
    val name: String?,
    val icon: String?,
    val longitude: Double?,
    val latitude: Double?,
    val sysId: Long = 0
)

data class LocationDetails(
    val place: PlaceDetails
)

data class PlaceDetails(
    val comments: String,
    val banner: String
)

data class Details(
    var id: Long,
    val name: String?,
    val comments: String,
    val banner: String
)


