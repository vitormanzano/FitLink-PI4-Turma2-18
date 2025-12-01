package br.edu.puc.fitlink.data.remote

import br.edu.puc.fitlink.data.model.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import br.edu.puc.fitlink.data.model.ResponseTrainDto
import retrofit2.http.GET
import retrofit2.http.Path



interface TrainApi {

    @GET("Train/getByClientId/{clientId}")
    suspend fun getTrainsByClientId(
        @Path("clientId") clientId: String
    ): List<ResponseTrainDto>

    @POST("Train/register")
    suspend fun register(
        @Body dto: RegisterTrainDto
    ): Response<ResponseTrainDto>

    @PATCH("Train/update/{trainId}")
    suspend fun updateTrain(
        @Path("trainId") trainId: String,
        @Body dto: UpdateTrainDto
    ): Response<ResponseTrainDto>

    @DELETE("Train/deleteById/{trainId}")
    suspend fun deleteTrain(
        @Path("trainId") trainId: String
    ): Response<Unit>
}

// ======== CLIENT API ========
interface ClientApi {

    // ---------- REGISTER / LOGIN ----------
    @POST("Client/register")
    suspend fun register(@Body client: RegisterClientDto): Response<ResponseBody>

    @POST("Client/login")
    suspend fun login(@Body client: LoginClientDto): Response<ClientResponseDto>

    // ---------- GET ----------
    @GET("Client/getById/{id}")
    suspend fun getById(@Path("id") id: String): Response<ClientResponseDto>

    // ---------- UPDATE ----------
    @PATCH("Client/update/{id}")
    suspend fun updateClient(
        @Path("id") id: String,
        @Body dto: UpdateClientDto
    ): Response<ClientResponseDto>

    // ---------- DELETE ----------
    @DELETE("Client/delete/{id}")
    suspend fun deleteClient(@Path("id") id: String): Response<ResponseBody>

    // ---------- LINK / UNLINK PERSONAL ----------
    @PATCH("Client/linkToPersonal/{clientId}/{personalTrainerId}")
    suspend fun linkPersonal(
        @Path("clientId") clientId: String,
        @Path("personalTrainerId") personalId: String
    ): Response<ResponseBody>

    @PATCH("Client/closeLinkWithPersonal/{clientId}")
    suspend fun closeLink(@Path("clientId") clientId: String): Response<ResponseBody>

    // ---------- ADD INFORMATIONS (PERFIL) ----------
    @PATCH("Client/AddInformations/{clientId}")
    suspend fun addInformations(
        @Path("clientId") clientId: String,
        @Body dto: MoreInformationsDto
    ): Response<ResponseBody>

    @GET("Client/getClientsByPersonalTrainer/{personalTrainerId}")
    suspend fun getClientsByPersonalTrainer(
        @Path("personalTrainerId") personalId: String
    ): Response<List<ClientResponseDto>>

    // ---------- VERIFY LINK ----------
    @GET("Client/verifyIfIsLinkedToPersonal/{clientId}/{personalTrainerId}")
    suspend fun verifyIfIsLinkedToPersonal(
        @Path("clientId") clientId: String,
        @Path("personalTrainerId") personalId: String
    ): Response<Boolean>

}


// ======== PERSONAL API ========
interface PersonalApi {

    @POST("Personal/register")
    suspend fun register(@Body dto: RegisterPersonalDto): Response<ResponseBody>

    @POST("Personal/login")
    suspend fun loginPersonal(
        @Body body: LoginPersonalDto
    ): Response<PersonalResponseDto>

    @GET("Personal/city/{city}")
    suspend fun getPersonalsByCity(
        @Path("city") city: String
    ): List<PersonalResponseDto>

    @GET("Personal/getById/{personalId}")
    suspend fun getById(
        @Path("personalId") personalId: String
    ): Response<PersonalResponseDto>

    @PATCH("Personal/update/{personalId}")   // <-- CORRIGIDO
    suspend fun update(
        @Path("personalId") id: String,
        @Body dto: UpdatePersonalDto
    ): Response<PersonalResponseDto>

    @DELETE("Personal/delete/{id}")          // <-- CORRIGIDO
    suspend fun delete(
        @Path("id") id: String
    ): Response<Unit>

    @PATCH("Personal/addMoreInformations/{personalId}")
    suspend fun addMoreInformations(
        @Path("personalId") personalId: String,
        @Body body: MoreInformationsPersonalDto
    ): Response<Unit>
}

interface MessageApi {

    @POST("Message/register")
    suspend fun register(
        @Body dto: RegisterMessageDto
    ): Response<ResponseMessageDto>

    @GET("Message/GetAllMessagesByPersonalId/{personalId}")
    suspend fun getAllMessagesByPersonalId(
        @Path("personalId") personalId: String
    ): Response<List<ResponseMessageDto>>

    @DELETE("Message/delete/{messageId}")
    suspend fun deleteMessage(
        @Path("messageId") messageId: String
    ): Response<ResponseBody>
}

// ======== RETROFIT INSTANCE ========
object RetrofitInstance {
    private const val BASE_URL = "http://10.0.2.2:5229/"

    // logging (mostra requisições e respostas no Logcat)
    private val client by lazy {
        val log = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        OkHttpClient.Builder()
            .addInterceptor(log)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // instâncias
    val clientApi: ClientApi by lazy { retrofit.create(ClientApi::class.java) }
    val personalApi: PersonalApi by lazy { retrofit.create(PersonalApi::class.java) }

    val messageApi: MessageApi by lazy { retrofit.create(MessageApi::class.java) }
}
