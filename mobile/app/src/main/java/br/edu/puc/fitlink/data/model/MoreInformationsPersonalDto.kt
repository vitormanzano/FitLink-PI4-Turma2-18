package br.edu.puc.fitlink.data.model

import com.google.gson.annotations.SerializedName

data class MoreInformationsPersonalDto(
    @SerializedName("aboutMe") val aboutMe: String?,
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("experience") val experience: String?
)

