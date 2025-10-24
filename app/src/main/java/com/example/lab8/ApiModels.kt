package com.example.lab8

import com.google.gson.annotations.SerializedName

// ============================================
// DTOs para Characters
// ============================================

data class CharactersResponse(
    @SerializedName("info")
    val info: ApiInfo,
    @SerializedName("results")
    val results: List<CharacterDto>
)

data class CharacterDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("species")
    val species: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("origin")
    val origin: OriginDto,
    @SerializedName("location")
    val location: LocationRefDto,
    @SerializedName("image")
    val image: String,
    @SerializedName("episode")
    val episode: List<String>,
    @SerializedName("url")
    val url: String,
    @SerializedName("created")
    val created: String
)

data class OriginDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)

data class LocationRefDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("url")
    val url: String
)

// ============================================
// DTOs para Locations
// ============================================

data class LocationsResponse(
    @SerializedName("info")
    val info: ApiInfo,
    @SerializedName("results")
    val results: List<LocationDto>
)

data class LocationDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("dimension")
    val dimension: String,
    @SerializedName("residents")
    val residents: List<String>,
    @SerializedName("url")
    val url: String,
    @SerializedName("created")
    val created: String
)

data class ApiInfo(
    @SerializedName("count")
    val count: Int,
    @SerializedName("pages")
    val pages: Int,
    @SerializedName("next")
    val next: String?,
    @SerializedName("prev")
    val prev: String?
)

// Mappers de DTO a Entity

fun CharacterDto.toEntity() = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    type = type,
    gender = gender,
    image = image
)

fun LocationDto.toEntity() = LocationEntity(
    id = id,
    name = name,
    type = type,
    dimension = dimension
)