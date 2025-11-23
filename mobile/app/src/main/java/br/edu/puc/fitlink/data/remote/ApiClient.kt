package br.edu.puc.fitlink.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // AJUSTE AQUI PARA A PORTA DO SEU BACKEND
    // 10.0.2.2 = localhost no emulador Android
    private const val BASE_URL = "http://10.0.2.2:5229/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val trainApi: TrainApi = retrofit.create(TrainApi::class.java)
}
