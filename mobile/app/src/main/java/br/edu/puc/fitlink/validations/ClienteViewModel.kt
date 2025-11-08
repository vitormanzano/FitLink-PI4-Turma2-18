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

    fun validarEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val socket = Socket("10.0.2.2", 3000)

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
}
