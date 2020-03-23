package com.example.exam.gson

data class Geometry(
    var type: String?,
    var coordinates: List<Double>?
)

data class ListFeed(
    val features: List<Location>
)

data class Location(
    var type: String?,
    var properties: Properties?,
    var geometry: Geometry?
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

