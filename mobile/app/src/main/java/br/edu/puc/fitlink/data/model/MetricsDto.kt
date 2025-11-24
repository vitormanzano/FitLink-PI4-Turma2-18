package br.edu.puc.fitlink.data.model

import com.google.gson.annotations.SerializedName

data class MetricsDto(
    @SerializedName(value = "Height", alternate = ["height"])
    val height: String,

    @SerializedName(value = "Weight", alternate = ["weight"])
    val weight: String
)
