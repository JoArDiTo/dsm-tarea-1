package com.fisi.tarea1.api

data class PlaceResult(
    val places: List<Place>
)

data class Place(
    val formattedAddress: String,
    val location: Location,
    val displayName: DisplayName
)

data class Location(
    val latitude: Double,
    val longitude: Double
)

data class DisplayName(
    val text: String
)