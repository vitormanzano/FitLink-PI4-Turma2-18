package br.edu.puc.fitlink.validations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

class ClienteViewModel : ViewModel() {

    private val host = "10.0.2.2"
    private val port = 3000

    // ==== CLIENTE ====
    // Envia: SIGNUP_CLIENT:nome;email;senha;telefone;cidade
    // Recebe: "OK" ou "ERRO:mensagem"
    fun validarSignUpClient(
        name: String,
        email: String,
        password: String,
        phone: String,
        city: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Socket(host, port).use { socket ->
                    val out = PrintWriter(socket.getOutputStream(), true)
                    val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                    val comando = "SIGNUP_CLIENT:$name;$email;$password;$phone;$city"
                    out.println(comando)

                    val resposta = input.readLine()
                    println("Resposta SIGNUP_CLIENT: $resposta")

                    val (valido, msgErro) = parseResposta(resposta)

                    withContext(Dispatchers.Main) {
                        onResult(valido, msgErro)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(false, "Falha na conexão com o servidor.")
                }
            }
        }
    }

    // ==== PERSONAL ====
    // Envia: SIGNUP_PERSONAL:nome;email;senha;telefone;cidade;cpf;cref
    // Recebe: "OK" ou "ERRO:mensagem"
    fun validarSignUpPersonal(
        name: String,
        email: String,
        password: String,
        phone: String,
        city: String,
        cpf: String,
        cref: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Socket(host, port).use { socket ->
                    val out = PrintWriter(socket.getOutputStream(), true)
                    val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                    val comando = "SIGNUP_PERSONAL:$name;$email;$password;$phone;$city;$cpf;$cref"
                    out.println(comando)

                    val resposta = input.readLine()
                    println("Resposta SIGNUP_PERSONAL: $resposta")

                    val (valido, msgErro) = parseResposta(resposta)

                    withContext(Dispatchers.Main) {
                        onResult(valido, msgErro)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(false, "Falha na conexão com o servidor.")
                }
            }
        }
    }

    // Se quiser manter o validarEmail antigo pra outros usos:
    fun validarEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Socket(host, port).use { socket ->
                    val out = PrintWriter(socket.getOutputStream(), true)
                    val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                    out.println("VALIDAR_EMAIL:$email")
                    val resposta = input.readLine()
                    println("Resposta VALIDAR_EMAIL: $resposta")

                    withContext(Dispatchers.Main) {
                        onResult(resposta == "OK")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    // Interpreta "OK" / "ERRO:mensagem"
    private fun parseResposta(resposta: String?): Pair<Boolean, String?> {
        if (resposta == null) return false to "Sem resposta do servidor."

        return when {
            resposta == "OK" -> true to null
            resposta.startsWith("ERRO:") -> {
                val msg = resposta.substringAfter("ERRO:").trim()
                false to msg.ifBlank { "Dados inválidos." }
            }
            else -> false to "Resposta inesperada do servidor."
        }
    }
}
