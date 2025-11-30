package br.edu.puc.fitlink.data.model

data class UpdatePersonalDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val city: String
)
