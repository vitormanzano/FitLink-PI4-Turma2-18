package br.edu.puc.fitlink.data.model

import com.google.gson.annotations.SerializedName

data class ClientResponseDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("city") val city: String,
    @SerializedName("aboutMe") val aboutMe: String?,
    @SerializedName("goals") val goals: String?,
    @SerializedName("metrics") val metrics: MetricsDto?
)
