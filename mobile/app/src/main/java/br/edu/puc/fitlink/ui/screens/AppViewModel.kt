package br.edu.puc.fitlink.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.puc.fitlink.data.model.ClientResponseDto
import br.edu.puc.fitlink.data.model.MetricsDto
import br.edu.puc.fitlink.data.model.MoreInformationsDto
import br.edu.puc.fitlink.data.model.PersonalResponseDto
import br.edu.puc.fitlink.data.model.ResponseTrainDto
import br.edu.puc.fitlink.data.remote.ApiClient
import br.edu.puc.fitlink.data.remote.RetrofitInstance
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
data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val nome: String = "",
    val bio: String = "",
    val objetivoTag: String = "",
    val altura: String = "",
    val peso: String = ""
)

class ProfileViewModel : ViewModel() {

    var state: ProfileUiState by mutableStateOf(ProfileUiState())
        private set

    private var clientId: String? = null

    fun loadProfile(id: String) {
        clientId = id

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)

            try {
                val resp = RetrofitInstance.clientApi.getById(id)

                if (resp.isSuccessful) {
                    val user: ClientResponseDto = resp.body()!!

                    Log.d("ProfileVM", "Resposta getById: $user")

                    state = state.copy(
                        isLoading = false,
                        nome = user.name,
                        bio = user.aboutMe ?: "",
                        objetivoTag = user.goals ?: "",
                        altura = user.metrics?.height ?: "",
                        peso = user.metrics?.weight ?: ""
                    )
                } else {
                    val err = resp.errorBody()?.string()
                    Log.e("ProfileVM", "Erro HTTP ${resp.code()} - $err")
                    state = state.copy(
                        isLoading = false,
                        error = err ?: "Erro ao carregar perfil"
                    )
                }
            } catch (e: Exception) {
                Log.e("ProfileVM", "Exceção ao carregar perfil", e)
                state = state.copy(
                    isLoading = false,
                    error = e.message ?: "Erro de rede"
                )
            }
        }
    }

    fun salvar(
        bio: String,
        goals: String,
        altura: String,
        peso: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val id = clientId ?: return

        val dto = MoreInformationsDto(
            aboutMe = bio,
            goals = goals,
            metrics = MetricsDto(
                height = altura,
                weight = peso
            )
        )

        viewModelScope.launch {
            try {
                val resp = RetrofitInstance.clientApi.addInformations(id, dto)

                if (resp.isSuccessful) {
                    state = state.copy(
                        bio = bio,
                        objetivoTag = goals,
                        altura = altura,
                        peso = peso
                    )
                    onResult(true, "Informações salvas!")
                } else {
                    onResult(false, resp.errorBody()?.string() ?: "Erro ao salvar")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Erro de rede")
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
