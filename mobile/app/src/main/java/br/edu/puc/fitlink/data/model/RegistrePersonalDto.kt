package br.edu.puc.fitlink.data.model

data class RegisterPersonalDto(
    val name: String,
    val email: String,
    val password: String,
    val phone: String,
    val city: String,
    val cpf: String,
    val cref: String
)
