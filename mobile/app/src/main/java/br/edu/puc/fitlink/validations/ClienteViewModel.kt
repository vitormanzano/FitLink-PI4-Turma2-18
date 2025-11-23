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

    fun validarEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val socket = Socket(host, port)

                val out = PrintWriter(socket.getOutputStream(), true)
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))

                out.println("VALIDAR_EMAIL:$email")
                val resposta = input.readLine()
                println("Resposta recebida: $resposta")

                socket.close()

                withContext(Dispatchers.Main) {
                    onResult(resposta == "OK")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

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

                    var valido = false
                    var mensagemErro: String? = null

                    if (resposta != null) {
                        if (resposta == "OK") {
                            valido = true
                        } else if (resposta.startsWith("ERRO:")) {
                            valido = false
                            mensagemErro = resposta.substringAfter("ERRO:").trim()
                        }
                    } else {
                        mensagemErro = "Sem resposta do servidor."
                    }

                    withContext(Dispatchers.Main) {
                        onResult(valido, mensagemErro)
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
}
