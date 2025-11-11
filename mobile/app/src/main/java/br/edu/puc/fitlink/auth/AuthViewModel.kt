package br.edu.puc.fitlink.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.puc.fitlink.data.model.*
import br.edu.puc.fitlink.data.remote.RetrofitInstance
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    fun register(dto: RegisterClientDto, onResult: (ok: Boolean, msg: String) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = RetrofitInstance.clientApi.register(dto) // <-- clientApi
                if (resp.isSuccessful) {
                    val body = resp.body()?.string() ?: "Cadastro realizado!"
                    onResult(true, body)
                } else {
                    onResult(false, resp.errorBody()?.string() ?: "Erro no cadastro")
                }
            } catch (e: Exception) {
                onResult(false, e.message ?: "Falha de rede")
            }
        }
    }

    fun login(dto: LoginClientDto, onResult: (ok: Boolean, user: ClientResponseDto?, erro: String?) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = RetrofitInstance.clientApi.login(dto) // <-- clientApi
                if (resp.isSuccessful) {
                    onResult(true, resp.body(), null)
                } else {
                    onResult(false, null, resp.errorBody()?.string() ?: "Login inválido")
                }
            } catch (e: Exception) {
                onResult(false, null, e.message ?: "Falha de rede")
            }
        }
    }

    fun registerPersonal(dto: RegisterPersonalDto, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.personalApi.register(dto) // <-- personalApi
                if (response.isSuccessful)
                    onResult(true, response.body() ?: "Personal cadastrado com sucesso!")
                else
                    onResult(false, response.errorBody()?.string() ?: "Erro no cadastro")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Falha de rede")
            }
        }
    }
}
