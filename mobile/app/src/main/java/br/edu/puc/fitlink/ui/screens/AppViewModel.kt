package br.edu.puc.fitlink.ui.screens

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.puc.fitlink.data.remote.ApiClient
import br.edu.puc.fitlink.data.model.ResponseTrainDto
import kotlinx.coroutines.launch
import android.util.Log
import retrofit2.HttpException
import java.io.IOException

data class WorkoutItem(val name: String, val series: String)
data class WorkoutGroup(val title: String, val items: List<WorkoutItem>)
data class Trainer(
    val id: String,
    val name: String,
    val specialty: String,
    val avatarUrl: String? = null
)

class AppViewModel : ViewModel() {

    var hasPersonal by mutableStateOf(true)
        private set

    var clientId by mutableStateOf<String?>(null)
        private set

    var workoutGroups by mutableStateOf<List<WorkoutGroup>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val trainers = listOf(
        Trainer("1", "Carlos Silva", "Hipertrofia"),
        Trainer("2", "Ana Souza", "Funcional", avatarUrl = null),
        Trainer("3", "Lucas Mendes", "Funcional")
    )

    fun updateClientId(id: String) {
        clientId = id
    }

    fun connectPersonal() { hasPersonal = true }
    fun disconnectPersonal() { hasPersonal = false }

    fun loadWorkouts() {
        val id = clientId ?: return
        if (!hasPersonal) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val train = ApiClient.trainApi.getTrainByClientId(id)
                workoutGroups = listOf(train.toWorkoutGroup())
            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        if (e.code() == 404) {
                            // ✔ sem treino ainda (não é erro!)
                            workoutGroups = emptyList()
                            errorMessage = null
                        } else {
                            errorMessage = "Erro ${e.code()} ao carregar treinos."
                            workoutGroups = emptyList()
                        }
                    }
                    is IOException -> {
                        errorMessage = "Falha de conexão com o servidor."
                        workoutGroups = emptyList()
                    }
                    else -> {
                        errorMessage = "Erro inesperado: ${e.message}"
                        workoutGroups = emptyList()
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }
}

private fun ResponseTrainDto.toWorkoutGroup(): WorkoutGroup {
    val items = exercises.map { exercise ->
        val setsCount = exercise.sets.size
        val reps = exercise.sets.firstOrNull()?.numberOfRepetitions

        val seriesText = if (reps != null)
            "$setsCount séries de $reps repetições"
        else
            "$setsCount séries"

        WorkoutItem(
            name = exercise.name,
            series = seriesText
        )
    }

    return WorkoutGroup(
        title = name,
        items = items
    )
}
