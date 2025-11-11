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

// ======== CLIENT API ========
interface ClientApi {
    @POST("Client/register")
    suspend fun register(@Body client: RegisterClientDto): Response<ResponseBody>

    @POST("Client/login")
    suspend fun login(@Body client: LoginClientDto): Response<ClientResponseDto>

    @GET("Client/getById/{id}")
    suspend fun getById(@Path("id") id: String): Response<ClientResponseDto>
}

// ======== PERSONAL API ========
interface PersonalApi {
    @POST("Personal/register")
    suspend fun register(@Body dto: RegisterPersonalDto): Response<String>
}

// ======== RETROFIT INSTANCE ========
object RetrofitInstance {

    // --- ajuste aqui sua URL ---
    // 🔹 local backend ASP.NET no emulador:
    // private const val BASE_URL = "http://10.0.2.2:5000/"
    // 🔹 ou com HTTPS:
    // private const val BASE_URL = "https://10.0.2.2:5001/"
    // 🔹 produção:
    private const val BASE_URL = "http://10.0.2.2:5229/" // mantenha a barra no final

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
}
