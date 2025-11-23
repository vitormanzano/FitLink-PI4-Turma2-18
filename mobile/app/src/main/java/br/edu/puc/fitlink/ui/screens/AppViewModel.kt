package br.edu.puc.fitlink.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.puc.fitlink.data.model.PersonalResponseDto
import br.edu.puc.fitlink.data.model.ResponseTrainDto
import br.edu.puc.fitlink.data.remote.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// ----------------- MODELOS DE UI -----------------

data class WorkoutItem(val name: String, val series: String)

data class WorkoutGroup(val title: String, val items: List<WorkoutItem>)

data class Trainer(
    val id: String,
    val name: String,
    val specialty: String,
    val avatarUrl: String? = null
)

// ----------------- APP VIEWMODEL (TREINOS / CLIENTE) -----------------

class AppViewModel : ViewModel() {

    var hasPersonal by mutableStateOf(true)
        private set

    var clientId by mutableStateOf<String?>(null)
        private set

    var isProfessor by mutableStateOf(true)
        private set

    var workoutGroups by mutableStateOf<List<WorkoutGroup>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    val trainers = listOf(
        Trainer("1", "Carlos Silva", "Hipertrofia"),
        Trainer("2", "Ana Souza", "Funcional"),
        Trainer("3", "Lucas Mendes", "Funcional")
    )

    fun updateClientId(id: String) {
        clientId = id
    }

    fun setIsProfessor(value: Boolean) {
        isProfessor = value
    }

    fun connectPersonal() {
        hasPersonal = true
    }

    fun disconnectPersonal() {
        hasPersonal = false
    }

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
                            // sem treino ainda (não é erro de sistema)
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

// ----------------- SEARCH VIEWMODEL (BUSCA DE PERSONAIS) -----------------

class SearchViewModel : ViewModel() {

    var searchResults by mutableStateOf<List<PersonalResponseDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun searchByCity(city: String) {
        if (city.isBlank()) {
            searchResults = emptyList()
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                Log.d("SearchViewModel", "Buscando personais da cidade: $city")
                val result = ApiClient.personalApi.getPersonalsByCity(city)
                Log.d("SearchViewModel", "Recebidos ${result.size} personais")
                searchResults = result
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SearchViewModel", "Erro ao buscar personais", e)

                searchResults = emptyList()

                errorMessage = when (e) {
                    is HttpException -> "Erro ${e.code()} ao buscar personais."
                    is IOException -> "Falha de conexão com o servidor."
                    else -> "Erro inesperado: ${e.message}"
                }
            } finally {
                isLoading = false
            }
        }
    }
}

// ----------------- PERSONAL DETAIL VIEWMODEL (DETALHE DO PERSONAL) -----------------

data class PersonalDetailUiState(
    val isLoading: Boolean = false,
    val personal: PersonalResponseDto? = null,
    val errorMessage: String? = null
)

class PersonalDetailViewModel : ViewModel() {

    var uiState by mutableStateOf(PersonalDetailUiState())
        private set

    fun loadPersonal(personalId: String) {
        if (personalId.isBlank()) {
            uiState = uiState.copy(errorMessage = "ID do personal inválido.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                Log.d("PersonalDetailViewModel", "Buscando personal com id: $personalId")

                // Usa o endpoint definido no PersonalApi
                val result = ApiClient.personalApi.getById(personalId)

                uiState = uiState.copy(
                    isLoading = false,
                    personal = result,
                    errorMessage = null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("PersonalDetailViewModel", "Erro ao buscar personal", e)

                uiState = uiState.copy(
                    isLoading = false,
                    personal = null,
                    errorMessage = when (e) {
                        is HttpException -> "Erro ${e.code()} ao carregar personal."
                        is IOException -> "Falha de conexão com o servidor."
                        else -> "Erro inesperado: ${e.message}"
                    }
                )
            }
        }
    }
}

// ----------------- EXTENSÃO PARA CONVERTER RESPONSETRAINDTO -----------------

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
