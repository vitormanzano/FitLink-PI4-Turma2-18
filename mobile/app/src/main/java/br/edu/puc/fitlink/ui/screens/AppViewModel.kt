package br.edu.puc.fitlink.ui.screens

import android.content.Context
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
import br.edu.puc.fitlink.data.model.MoreInformationsPersonalDto
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
import br.edu.puc.fitlink.data.model.UpdatePersonalDto
import br.edu.puc.fitlink.data.model.UpdateTrainDto
import br.edu.puc.fitlink.data.remote.PersonalApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

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
                // NOVA API: lista de treinos do aluno
                val trains = ApiClient.trainApi.getTrainsByClientId(id)

                // 🔹 usa o MESMO agrupador do personal
                workoutGroups = groupTrainsForUi(trains)

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

    fun logoutClient(context: Context) {
        clientId = null
        hasPersonal = false
        isProfessor = false

        // limpa SharedPreferences
        val prefs = context.getSharedPreferences("client_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()

        val prefsPersonal = context.getSharedPreferences("personal_prefs", Context.MODE_PRIVATE)
        prefsPersonal.edit().clear().apply()
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

    // exposto para a UI: null = verificando/unknown, true = já é aluno, false = não é aluno
    var isLinkedToPersonal by mutableStateOf<Boolean?>(null)
        internal set

    fun loadPersonal(personalId: String) {
        if (personalId.isBlank()) {
            uiState = uiState.copy(errorMessage = "ID do personal inválido.")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                Log.d("PersonalDetailViewModel", "Buscando personal com id: $personalId")

                val resp = RetrofitInstance.personalApi.getById(personalId)

                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        uiState = uiState.copy(
                            isLoading = false,
                            personal = body,
                            errorMessage = null
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            personal = null,
                            errorMessage = "Resposta vazia do servidor."
                        )
                    }
                } else {
                    val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                    uiState = uiState.copy(
                        isLoading = false,
                        personal = null,
                        errorMessage = err ?: "Erro ${resp.code()} ao carregar personal."
                    )
                }

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

    /**
     * Verifica se o clientId está vinculado ao personalId.
     *
     * Estratégia:
     * 1) tenta endpoint verifyIfIsLinkedToPersonal
     * 2) se não der (400/404/exceção), faz fallback para getClientsByPersonalTrainer(personalId)
     *    e procura clientId na lista retornada.
     *
     * clientIdRaw pode ser null (usuário não logado) -> nesse caso definimos false (não vinculado).
     * personalIdRaw deve ser o id do personal (string).
     */
    fun checkIfLinked(clientIdRaw: String?, personalIdRaw: String) {
        val clientId = clientIdRaw?.trim()
        val personalId = personalIdRaw.trim()

        Log.d("PersonalDetailVM", "checkIfLinked -> clientId='$clientId', personalId='$personalId'")

        // enquanto verifica, coloca null (loader)
        isLinkedToPersonal = null

        viewModelScope.launch {
            try {
                // 1) tentativa direta
                try {
                    val resp = RetrofitInstance.clientApi.verifyIfIsLinkedToPersonal(clientId ?: "", personalId)
                    val body = try { resp.body() } catch (_: Exception) { null }
                    val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }

                    Log.d("PersonalDetailVM", "verifyIfIsLinkedToPersonal -> isSuccessful=${resp.isSuccessful}, code=${resp.code()}, body=$body, error=$err")

                    if (resp.isSuccessful && body != null) {
                        isLinkedToPersonal = (body == true)
                        Log.d("PersonalDetailVM", "Resultado direto: $isLinkedToPersonal")
                        return@launch
                    } else {
                        Log.w("PersonalDetailVM", "verifyIfIsLinkedToPersonal devolveu não-success -> fallback. code=${resp.code()}")
                    }
                } catch (e: Exception) {
                    Log.w("PersonalDetailVM", "verifyIfIsLinkedToPersonal exceção -> fallback", e)
                }

                // 2) fallback: lista de clients do personal
                try {
                    Log.d("PersonalDetailVM", "Fallback: chamando getClientsByPersonalTrainer($personalId)")
                    val respList = RetrofitInstance.clientApi.getClientsByPersonalTrainer(personalId)

                    if (!respList.isSuccessful) {
                        val err = try { respList.errorBody()?.string() } catch (_: Exception) { null }
                        Log.e("PersonalDetailVM", "getClientsByPersonalTrainer erro http ${respList.code()} -> $err")
                        isLinkedToPersonal = false
                        return@launch
                    }

                    val clients = respList.body().orEmpty()
                    Log.d("PersonalDetailVM", "getClientsByPersonalTrainer -> ${clients.size} clientes retornados")

                    // compara clientId (string GUID) com os ids retornados
                    val found = clientId?.let { cid ->
                        clients.any { c ->
                            // tenta comparar com diferentes nomes de propriedade (id / Id) por segurança
                            val remoteId = when {
                                // Kotlin generated property usually 'id' lowercase — adapte se necessário
                                // aqui acessamos as possíveis propriedades através do reflection-safe checks:
                                // mas para performance, assumimos 'id' está presente.
                                else -> try {
                                    // campo esperado: id (String)
                                    val fieldVal = c::class.java.getDeclaredField("id")
                                    fieldVal.isAccessible = true
                                    (fieldVal.get(c) ?: "").toString()
                                } catch (_: Exception) {
                                    try {
                                        val fieldVal = c::class.java.getDeclaredField("Id")
                                        fieldVal.isAccessible = true
                                        (fieldVal.get(c) ?: "").toString()
                                    } catch (_: Exception) {
                                        null
                                    }
                                }
                            }
                            remoteId?.equals(cid, ignoreCase = true) == true
                        }
                    } ?: false

                    isLinkedToPersonal = found
                    Log.d("PersonalDetailVM", "Resultado fallback (encontrado?): $found")
                } catch (e: Exception) {
                    Log.e("PersonalDetailVM", "Erro no fallback getClientsByPersonalTrainer", e)
                    isLinkedToPersonal = false
                }
            } catch (e: Exception) {
                Log.e("PersonalDetailVM", "Erro inesperado em checkIfLinked", e)
                isLinkedToPersonal = false
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

    // guarda TODOS os treinos do aluno
    private var trainList: List<ResponseTrainDto> = emptyList()

    fun loadWorkoutsForStudent(clientId: String) {
        if (clientId.isBlank()) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // NOVA API: retorna LISTA de treinos
                val trains = ApiClient.trainApi.getTrainsByClientId(clientId)

                trainList = trains

                // 🔹 monta grupos já AGRUPANDO por nome do treino (Treino Segunda, Treino Terça, ...)
                workoutGroups = groupTrainsForUi(trainList)

            } catch (e: Exception) {
                when (e) {
                    is HttpException -> {
                        if (e.code() == 404) {
                            workoutGroups = emptyList()
                            errorMessage = null
                        } else {
                            workoutGroups = emptyList()
                            errorMessage = "Erro ${e.code()} ao carregar treinos."
                        }
                    }
                    is IOException -> {
                        workoutGroups = emptyList()
                        errorMessage = "Falha de conexão com o servidor."
                    }
                    else -> {
                        workoutGroups = emptyList()
                        errorMessage = "Erro inesperado: ${e.message}"
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun removeExercise(group: WorkoutGroup, item: WorkoutItem) {

        // 🔍 Descobre exatamente em qual treino e exercício está esse item
        val pair = trainList
            .asSequence()
            .flatMap { train ->
                train.exercises.map { ex -> train to ex }
            }
            .firstOrNull { (_, ex) ->
                val setsCount = ex.sets.size
                val reps = ex.sets.firstOrNull()?.numberOfRepetitions
                val seriesText = if (reps != null)
                    "$setsCount séries de $reps repetições"
                else
                    "$setsCount séries"

                ex.name == item.name && item.series == seriesText
            }

        if (pair == null) {
            Log.e("WORKOUT_DEBUG_REMOVE", "Não achou exercício para remover: ${item.name}")
            return
        }

        val (train, targetExercise) = pair

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                // remove só o exercício clicado
                val filteredExercises = train.exercises.filterNot { it == targetExercise }

                if (filteredExercises.isEmpty()) {
                    // 🔥 se não sobrou nenhum exercício, apaga o treino inteiro
                    val respDelete = ApiClient.trainApi.deleteTrain(train.id)
                    if (!respDelete.isSuccessful) {
                        throw Exception(respDelete.errorBody()?.string() ?: "Erro ao apagar treino")
                    }

                    // remove o treino da lista local
                    trainList = trainList.filterNot { it.id == train.id }
                } else {
                    // ✅ ainda tem exercícios -> faz UPDATE normal
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
                        name = train.name,
                        clientId = train.clientId,
                        personalId = train.personalId,
                        exercises = updateExercises
                    )

                    val resp = ApiClient.trainApi.updateTrain(train.id, updateDto)
                    if (!resp.isSuccessful) {
                        throw Exception(resp.errorBody()?.string() ?: "Erro ao atualizar treino")
                    }

                    val updatedTrain = resp.body()!!

                    // substitui na lista local
                    trainList = trainList.map {
                        if (it.id == updatedTrain.id) updatedTrain else it
                    }
                }

                // 🔄 Recalcula grupos AGRUPANDO por dia / nome
                workoutGroups = groupTrainsForUi(trainList)

            } catch (e: Exception) {
                Log.e("WORKOUT_DEBUG_REMOVE", "ERRO AO REMOVER EXERCÍCIO", e)
                errorMessage = e.message ?: "Erro ao remover exercício"
            } finally {
                isLoading = false
            }
        }
    }
}

/**
 * Agrupa todos os treinos pela propriedade `name` (Treino Segunda, Treino Terça, ...)
 * e junta todos os exercícios daquele dia num único grupo.
 */
private fun groupTrainsForUi(trains: List<ResponseTrainDto>): List<WorkoutGroup> {
    val map = linkedMapOf<String, MutableList<WorkoutItem>>()

    for (t in trains) {
        val listForDay = map.getOrPut(t.name) { mutableListOf() }

        t.exercises.forEach { ex ->
            val setsCount = ex.sets.size
            val reps = ex.sets.firstOrNull()?.numberOfRepetitions

            val seriesText = if (reps != null)
                "$setsCount séries de $reps repetições"
            else
                "$setsCount séries"

            listForDay += WorkoutItem(
                name = ex.name,
                series = seriesText
            )
        }
    }

    // monta a lista final de grupos (só com dias que têm exercício)
    return map.map { (title, items) ->
        WorkoutGroup(
            title = title,
            items = items
        )
    }.filter { it.items.isNotEmpty() }
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

            Log.e("WORKOUT_DEBUG", "=== INICIANDO SALVAR TREINO ===")
            Log.e("WORKOUT_DEBUG", "Aluno: $studentId")
            Log.e("WORKOUT_DEBUG", "Personal: $personalId")
            Log.e("WORKOUT_DEBUG", "Treino: Treino $diaSemana")
            Log.e("WORKOUT_DEBUG", "Exercicio: $exercicio / Series: $series / Reps: $repeticoes")

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

                Log.e("WORKOUT_DEBUG", "DTO ENVIADO: $dto")

                val resp = ApiClient.trainApi.register(dto)

                Log.e("WORKOUT_DEBUG", "RESPOSTA HTTP: ${resp.code()}")

                if (!resp.isSuccessful) {
                    val err = resp.errorBody()?.string()
                    Log.e("WORKOUT_DEBUG", "ERRO DO BACKEND: $err")

                    errorMessage = err ?: "Erro ao registrar treino"
                    onFinished(false)
                    return@launch
                }

                val body = resp.body()
                Log.e("WORKOUT_DEBUG", "TREINO CRIADO COM SUCESSO: $body")

                successMessage = "Treino salvo com sucesso!"
                onFinished(true)

            } catch (e: Exception) {

                Log.e("WORKOUT_DEBUG", "EXCEÇÃO AO SALVAR: ${e.message}", e)

                errorMessage = e.message ?: "Erro ao salvar treino"
                onFinished(false)

            } finally {
                isSaving = false
            }
        }
    }
}


data class PersonalUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val personal: PersonalResponseDto? = null,
    val errorMessage: String? = null
)
class PersonalViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PersonalUiState())
    val uiState: StateFlow<PersonalUiState> = _uiState.asStateFlow()

    fun loadPersonal(personalId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                Log.d("PersonalVM", "loadPersonal: $personalId")
                val resp = ApiClient.personalApi.getById(personalId)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    if (body != null) {
                        _uiState.value = _uiState.value.copy(personal = body, isLoading = false)
                    } else {
                        _uiState.value = _uiState.value.copy(personal = null, isLoading = false, errorMessage = "Resposta vazia do servidor.")
                    }
                } else {
                    val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                    _uiState.value = _uiState.value.copy(personal = null, isLoading = false, errorMessage = err ?: "Erro ${resp.code()} ao carregar personal.")
                }
            } catch (e: Exception) {
                Log.e("PersonalVM", "Erro loadPersonal", e)
                val msg = when (e) {
                    is HttpException -> "Erro ${e.code()} ao carregar personal."
                    is IOException -> "Falha de conexão com o servidor."
                    else -> e.message ?: "Erro inesperado"
                }
                _uiState.value = _uiState.value.copy(personal = null, isLoading = false, errorMessage = msg)
            }
        }
    }

    /**
     * Atualiza apenas os campos de MoreInformations (aboutMe, specialization, experience)
     * usando o endpoint PATCH addMoreInformations/{personalId}
     * onResult: (success: Boolean, message: String?)
     */
    fun updatePersonal(
        personalId: String,
        aboutMe: String?,
        specialization: String?,
        experience: String?,
        callback: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

            try {
                val dto = MoreInformationsPersonalDto(
                    aboutMe = aboutMe?.takeIf { it.isNotBlank() },
                    specialization = specialization?.takeIf { it.isNotBlank() },
                    experience = experience?.takeIf { it.isNotBlank() }
                )

                Log.d("PersonalVM", "Enviando addMoreInformations DTO: $dto")

                val resp = ApiClient.personalApi.addMoreInformations(personalId, dto)

                if (resp.isSuccessful) {
                    // recarrega os dados
                    loadPersonal(personalId)

                    _uiState.value = _uiState.value.copy(isSaving = false)

                    // chama callback na Main thread (segurança)
                    withContext(Dispatchers.Main) {
                        Log.d("PersonalVM", "updatePersonal success -> chamando callback(true)")
                        callback(true, "Informações atualizadas com sucesso")
                    }
                } else {
                    val err = try { resp.errorBody()?.string() } catch (_: Exception) { null }
                    val msg = err ?: "Erro ${resp.code()} ao atualizar"
                    _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = msg)

                    withContext(Dispatchers.Main) {
                        Log.d("PersonalVM", "updatePersonal failed -> $msg")
                        callback(false, msg)
                    }
                }

            } catch (e: Exception) {
                val msg = e.message ?: "Erro inesperado"
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = msg)

                withContext(Dispatchers.Main) {
                    Log.e("PersonalVM", "Exceção updatePersonal", e)
                    callback(false, msg)
                }
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
