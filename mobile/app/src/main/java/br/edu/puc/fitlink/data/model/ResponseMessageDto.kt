package br.edu.puc.fitlink.data.model

data class ResponseMessageDto(
    val id: String,
    val clientId: String,
    val personalId: String,
    val hasAccepted: Boolean
)