package br.edu.puc.fitlink.data.model

// ---------- RESPOSTA DO BACKEND (GET) ----------

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

// ---------- DTOs PARA REGISTRO (POST /Train/register) ----------

data class RegisterSetDto(
    val number: Int,
    val numberOfRepetitions: Int,
    val weight: Double
)

data class RegisterExerciseDto(
    val name: String,
    val instructions: String,
    val sets: List<RegisterSetDto>
)

data class RegisterTrainDto(
    val name: String,
    val clientId: String,
    val personalId: String,
    val exercises: List<RegisterExerciseDto>
)

// ---------- DTO PARA UPDATE (PATCH /Train/update/{trainId}) ----------
// Se no backend algum campo não for opcional, depois é só tirar o "?" aqui.

data class UpdateTrainDto(
    val name: String? = null,
    val clientId: String? = null,
    val personalId: String? = null,
    val exercises: List<RegisterExerciseDto>? = null
)
