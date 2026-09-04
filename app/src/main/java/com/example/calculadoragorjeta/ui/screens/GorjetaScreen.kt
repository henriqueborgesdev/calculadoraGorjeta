package com.example.calculadoragorjeta.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado da tela: tudo o que ela precisa saber para se desenhar.
 *
 * A classe é imutável (somente `val`): sempre que algo muda, uma nova
 * instância é criada com .copy() dentro do ViewModel — nunca alteramos um
 * campo isolado.
 */
data class GorjetaUiState(
    val valorConta: String = "",
    val percentualGorjeta: String = "",
    val numeroPessoas: String = "",
    val valorGorjeta: Double? = null,
    val valorTotal: Double? = null,
    val valorPorPessoa: Double? = null,
    val mensagemErro: String? = null
)

/**
 * ViewModel da Calculadora de Gorjeta.
 *
 * Concentra TODA a lógica da tela: conversão de texto para número
 * (toDoubleOrNull()/toIntOrNull()), validação dos campos e os três cálculos.
 */
class GorjetaViewModel : ViewModel() {

    // Estado mutável: privado, só o ViewModel escreve nele.
    private val _uiState = MutableStateFlow(GorjetaUiState())

    // Versão somente-leitura exposta para a tela.
    val uiState: StateFlow<GorjetaUiState> = _uiState.asStateFlow()

    fun onValorContaChange(valor: String) {
        _uiState.value = _uiState.value.copy(valorConta = valor, mensagemErro = null)
    }

    fun onPercentualGorjetaChange(valor: String) {
        _uiState.value = _uiState.value.copy(percentualGorjeta = valor, mensagemErro = null)
    }

    fun onNumeroPessoasChange(valor: String) {
        _uiState.value = _uiState.value.copy(numeroPessoas = valor, mensagemErro = null)
    }

    fun calcular() {
        val conta = _uiState.value.valorConta.toDoubleOrNull()
        val percentual = _uiState.value.percentualGorjeta.toDoubleOrNull()
        // Número de pessoas é uma contagem: convertemos com toIntOrNull().
        val pessoas = _uiState.value.numeroPessoas.toIntOrNull()

        if (conta == null || percentual == null || pessoas == null) {
            mostrarErro("Preencha todos os campos com valores válidos.")
            return
        }
        if (conta <= 0.0) {
            mostrarErro("O valor da conta deve ser maior que zero.")
            return
        }
        if (pessoas < 1) {
            mostrarErro("Informe ao menos 1 pessoa.")
            return
        }
        if (percentual < 0.0) {
            mostrarErro("O percentual de gorjeta não pode ser negativo.")
            return
        }

        val gorjeta = conta * (percentual / 100)
        val total = conta + gorjeta

        _uiState.value = _uiState.value.copy(
            valorGorjeta = gorjeta,
            valorTotal = total,
            valorPorPessoa = total / pessoas,
            mensagemErro = null
        )
    }

    /**
     * Guarda a mensagem de erro e limpa os resultados anteriores, para que a
     * tela nunca exiba um resultado desatualizado ao lado de um erro.
     */
    private fun mostrarErro(mensagem: String) {
        _uiState.value = _uiState.value.copy(
            valorGorjeta = null,
            valorTotal = null,
            valorPorPessoa = null,
            mensagemErro = mensagem
        )
    }
}

/**
 * Tela da Calculadora de Gorjeta: três campos, um botão de cálculo e a
 * exibição dos resultados ou da mensagem de erro.
 *
 * Este Composable obtém o ViewModel com viewModel(), observa o uiState com
 * collectAsStateWithLifecycle() e encaminha eventos — nenhuma conversão,
 * validação ou cálculo acontece aqui.
 */
@Composable
fun GorjetaScreen(
    onVoltar: () -> Unit,
    viewModel: GorjetaViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Calculadora de Gorjeta", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.valorConta,
            onValueChange = viewModel::onValorContaChange,
            label = { Text("Valor da conta (R$)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.percentualGorjeta,
            onValueChange = viewModel::onPercentualGorjetaChange,
            label = { Text("Percentual de gorjeta (%)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = uiState.numeroPessoas,
            onValueChange = viewModel::onNumeroPessoasChange,
            label = { Text("Número de pessoas") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { viewModel.calcular() }) {
            Text("Calcular")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Área de resultado: a mensagem de erro da validação ou os três
        // valores calculados, arredondados para duas casas na exibição.
        uiState.mensagemErro?.let { erro ->
            Text(text = erro, color = MaterialTheme.colorScheme.error)
        }
        uiState.valorGorjeta?.let { gorjeta ->
            Text(
                text = "Valor da gorjeta: R$ %.2f".format(gorjeta),
                style = MaterialTheme.typography.titleMedium
            )
        }
        uiState.valorTotal?.let { total ->
            Text(
                text = "Valor total: R$ %.2f".format(total),
                style = MaterialTheme.typography.titleMedium
            )
        }
        uiState.valorPorPessoa?.let { porPessoa ->
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Valor por pessoa: R$ %.2f".format(porPessoa),
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = onVoltar) {
            Text("Voltar ao menu")
        }
    }
}
