package br.edu.puc.fitlink.data.model

data class ResponseSetDto(
    val number: Int,
    val numberOfRepetitions: Int,
    val weight: Double
)

data class ResponseExerciseDto(
    val name: String,
    val instructions: String,
    val sets: List<ResponseSetDto>
)

data class ResponseTrainDto(
    val id: String,
    val name: String,
    val clientId: String,
    val personalId: String,
    val exercises: List<ResponseExerciseDto>
)
