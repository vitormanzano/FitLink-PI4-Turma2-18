package br.edu.puc.fitlink.data.model

import com.google.gson.annotations.SerializedName

data class MetricsDto(
    @SerializedName("height") val height: String?,
    @SerializedName("weight") val weight: String?
)
