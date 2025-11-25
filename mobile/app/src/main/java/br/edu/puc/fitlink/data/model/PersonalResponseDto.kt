package br.edu.puc.fitlink.data.model

data class PersonalResponseDto(
    val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val city: String?,
    val specialty: String?,
    val avatarUrl: String?
)

