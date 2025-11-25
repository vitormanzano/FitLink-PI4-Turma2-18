package br.edu.puc.fitlink.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.puc.fitlink.R
import br.edu.puc.fitlink.data.model.ClientResponseDto
import br.edu.puc.fitlink.data.model.MetricsDto
import br.edu.puc.fitlink.data.model.MoreInformationsDto
import br.edu.puc.fitlink.data.model.PersonalResponseDto
import br.edu.puc.fitlink.data.model.RegisterExerciseDto
import br.edu.puc.fitlink.data.model.ResponseTrainDto
import br.edu.puc.fitlink.data.remote.ApiClient
import br.edu.puc.fitlink.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import br.edu.puc.fitlink.data.model.RegisterMessageDto
import br.edu.puc.fitlink.data.model.RegisterSetDto
import br.edu.puc.fitlink.data.model.RegisterTrainDto
import br.edu.puc.fitlink.data.model.ResponseMessageDto
import br.edu.puc.fitlink.data.model.UpdateTrainDto

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

class MessageViewModel : ViewModel() {

    fun enviarSolicitacao(
        clientId: String,
        personalId: String,
        onResult: (ok: Boolean, msg: String) -> Unit
    ) {
        val dto = RegisterMessageDto(
            clientId = clientId,
            personalId = personalId
        )

        viewModelScope.launch {
            try {
                val resp = RetrofitInstance.messageApi.register(dto)

                if (resp.isSuccessful) {
                    onResult(true, "Solicitação enviada ao personal!")
                } else {
                    onResult(false, resp.errorBody()?.string() ?: "Erro ao enviar solicitação")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Erro de rede ao enviar solicitação")
            }
        }
    }
}

data class AlunoInteressado(
    val messageId: String,
    val clientId: String,
    val nome: String,
    val cidade: String,
    val fotoRes: Int = R.drawable.ic_male, // por enquanto default
    val hasAccepted: Boolean
)

class NewStudentsViewModel : ViewModel() {

    var alunos by mutableStateOf<List<AlunoInteressado>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadInteressados(personalId: String) {
        if (personalId.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val resp = RetrofitInstance.messageApi.getAllMessagesByPersonalId(personalId)

                if (!resp.isSuccessful) {
                    errorMessage = resp.errorBody()?.string()
                        ?: "Erro ao carregar solicitações."
                    alunos = emptyList()
                    return@launch
                }

                val messages = resp.body().orEmpty()

                // Se quiser só os NÃO aceitos:
                val pendentes = messages.filter { !it.hasAccepted }

                val lista = mutableListOf<AlunoInteressado>()

                for (msg: ResponseMessageDto in pendentes) {
                    // busca dados do aluno
                    try {
                        val clientResp = RetrofitInstance.clientApi.getById(msg.clientId)
                        if (clientResp.isSuccessful) {
                            val client: ClientResponseDto? = clientResp.body()
                            if (client != null) {
                                lista.add(
                                    AlunoInteressado(
                                        messageId = msg.id,
                                        clientId = msg.clientId,
                                        nome = client.name,
                                        cidade = client.city ?: "Cidade não informada",
                                        fotoRes = R.drawable.ic_male, // futuramente pode trocar por genero
                                        hasAccepted = msg.hasAccepted
                                    )
                                )
                            }
                        }
                    } catch (_: Exception) {
                        // se um aluno der erro, apenas pula ele
                    }
                }

                alunos = lista

            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = when (e) {
                    is HttpException -> "Erro ${e.code()} ao carregar solicitações."
                    is IOException -> "Falha de conexão com o servidor."
                    else -> "Erro inesperado: ${e.message}"
                }
                alunos = emptyList()
            } finally {
                isLoading = false
            }
        }
    }
}

data class Aluno(
    val id: String,
    val nome: String,
    val cidade: String,
    val fotoRes: Int
)

class MyStudentsViewModel : ViewModel() {

    var alunos by mutableStateOf<List<Aluno>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadMyStudents(personalId: String) {
        if (personalId.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val resp = RetrofitInstance.clientApi.getClientsByPersonalTrainer(personalId)

                if (!resp.isSuccessful) {
                    errorMessage = resp.errorBody()?.string()
                        ?: "Erro ao carregar alunos."
                    alunos = emptyList()
                    return@launch
                }

                val clients = resp.body().orEmpty()

                alunos = clients.map { client ->
                    Aluno(
                        id = client.id,
                        nome = client.name,
                        cidade = client.city ?: "Cidade não informada",
                        fotoRes = R.drawable.ic_male // depois pode mudar p/ genero
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Erro inesperado ao carregar alunos."
                alunos = emptyList()
            } finally {
                isLoading = false
            }
        }
    }
}

class StudentsWorkoutViewModel : ViewModel() {

    var workoutGroups by mutableStateOf<List<WorkoutGroup>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // treino completo vindo do backend (para poder editar)
    private var currentTrain: ResponseTrainDto? = null

    fun loadWorkoutsForStudent(clientId: String) {
        if (clientId.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val train = ApiClient.trainApi.getTrainByClientId(clientId)

                currentTrain = train
                // usa a MESMA extensão que você já tem no AppViewModel:
                // private fun ResponseTrainDto.toWorkoutGroup(): WorkoutGroup
                workoutGroups = listOf(train.toWorkoutGroup())

            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        if (e.code() == 404) {
                            // sem treino ainda
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

    fun removeExercise(group: WorkoutGroup, item: WorkoutItem) {
        val train = currentTrain ?: return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // 1. monta a lista de exercícios SEM o que foi clicado
                val filteredExercises = train.exercises.filterNot { exercise ->
                    val setsCount = exercise.sets.size
                    val reps = exercise.sets.firstOrNull()?.numberOfRepetitions

                    val seriesText = if (reps != null)
                        "$setsCount séries de $reps repetições"
                    else
                        "$setsCount séries"

                    exercise.name == item.name && seriesText == item.series
                }

                // se não removeu nada, nem tenta atualizar
                if (filteredExercises.size == train.exercises.size) {
                    isLoading = false
                    return@launch
                }

                // 2. converte para DTO de update (RegisterExerciseDto / RegisterSetDto)
                val updateExercises = filteredExercises.map { ex ->
                    RegisterExerciseDto(
                        name = ex.name,
                        instructions = ex.instructions,
                        sets = ex.sets.map { s ->
                            RegisterSetDto(
                                number = s.number,
                                numberOfRepetitions = s.numberOfRepetitions,
                                weight = s.weight
                            )
                        }
                    )
                }

                val updateDto = UpdateTrainDto(
                    name = train.name,                 // mantém o nome
                    clientId = train.clientId,         // mantém cliente
                    personalId = train.personalId,     // mantém personal
                    exercises = updateExercises        // exercícios atualizados
                )

                // 3. chama o PATCH no backend
                val resp = ApiClient.trainApi.updateTrain(train.id, updateDto)

                if (!resp.isSuccessful) {
                    throw Exception(resp.errorBody()?.string() ?: "Erro ao atualizar treino")
                }

                // 4. atualiza estado local com a resposta do backend
                val updatedTrain = resp.body()!!
                currentTrain = updatedTrain
                workoutGroups = listOf(updatedTrain.toWorkoutGroup())

            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro ao remover exercício"
            } finally {
                isLoading = false
            }
        }
    }
}

class EditStudentsWorkoutViewModel : ViewModel() {

    var isSaving by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var successMessage by mutableStateOf<String?>(null)
        private set

    fun salvarTreino(
        studentId: String,
        personalId: String,
        diaSemana: String,
        exercicio: String,
        series: Int,
        repeticoes: Int,
        onFinished: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            successMessage = null

            try {

                val dto = RegisterTrainDto(
                    name = "Treino $diaSemana",
                    clientId = studentId,
                    personalId = personalId,
                    exercises = listOf(
                        RegisterExerciseDto(
                            name = exercicio,
                            instructions = "",
                            sets = List(series) { index ->
                                RegisterSetDto(
                                    number = index + 1,
                                    numberOfRepetitions = repeticoes,
                                    weight = 0.0
                                )
                            }
                        )
                    )
                )

                val resp = ApiClient.trainApi.register(dto)

                if (!resp.isSuccessful) {
                    throw Exception(resp.errorBody()?.string() ?: "Erro ao registrar treino")
                }

                successMessage = "Treino salvo com sucesso!"
                onFinished(true)

            } catch (e: Exception) {
                errorMessage = e.message ?: "Erro ao salvar treino"
                onFinished(false)

            } finally {
                isSaving = false
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
