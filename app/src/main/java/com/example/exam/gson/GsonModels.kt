package com.example.exam.gson

class Geometry(
    var type: String? = null,
    var coordinates: List<Double>? = null
)

class ListFeed(
    val features: List<Location>
)

class Location(
    var type: String? = null,
    var properties: Properties? = null,
    var geometry: Geometry? = null
)

class LocationDetails(
    val place: PlaceDetails
)

class PlaceDetails(
    val comments: String,
    val banner: String
)

class Properties(
    var name: String? = null,
    var icon: String? = null,
    var id: Long? = null
)

class Type(
    var type: String? = null,
    var features: List<Location>? = null
)
