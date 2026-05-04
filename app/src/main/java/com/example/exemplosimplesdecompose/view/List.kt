package com.example.exemplosimplesdecompose.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.exemplosimplesdecompose.R
import androidx.compose.ui.unit.dp
import android.content.Context
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.data.PostoStorage
import com.example.exemplosimplesdecompose.model.Posto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDePostos(navController: NavHostController, nomeDoPosto: String) {
    // Contexto necessário para iniciar a Intent do mapa

    val context = LocalContext.current
    val storage = remember { PostoStorage(context) }
    val listaDePostos = storage.buscarPostos()
    // Seus dados mockados

    val postoN = Posto(nomeDoPosto) // Se não passamos coordenadas, assumimos que é null ou vazia

    // Esta é a lista que vamos usar de fato


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.lista_postos)) }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            // 1. Mudamos de 'postos' para 'postosComp' e chamamos o item de 'posto'
            items(listaDePostos) { posto ->
                // --- 1. MATEMÁTICA AUTOMÁTICA PARA A LISTA ---
                val valorAlcool = posto.precoAlcool.toDoubleOrNull() ?: 0.0
                val valorGasolina = posto.precoGasolina.toDoubleOrNull() ?: 0.0

                // Ele vai no celular verificar se você deixou a chavinha dos 75% ligada
                val prefs =
                    LocalContext.current.getSharedPreferences("PostosPrefs", Context.MODE_PRIVATE)
                val usar75 = prefs.getBoolean("estado_switch_75", false)
                val taxaRendimento = if (usar75) 0.75 else 0.70

                val recomendacao = if (valorAlcool > 0.0 && valorGasolina > 0.0) {
                    if (valorAlcool <= (valorGasolina * taxaRendimento)) {
                        "⛽ Sugestão: ÁLCOOL"
                    } else {
                        "⛽ Sugestão: GASOLINA"
                    }
                } else {
                    ""
                }
                // ---------------------------------------------

                // Formatação da Data (que você já tinha)
                val formatoData =
                    java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                val dataFormatada = formatoData.format(java.util.Date(posto.dataCadastro))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    onClick = {
                        // A sua lógica do Mapa continua igual aqui dentro!
                        val uriString = if (posto.coordenadas != null) {
                            "geo:${posto.coordenadas.latitude},${posto.coordenadas.longitude}?q=${posto.coordenadas.latitude},${posto.coordenadas.longitude}(${
                                android.net.Uri.encode(
                                    posto.nome
                                )
                            })"
                        } else {
                            "geo:0,0?q=${android.net.Uri.encode(posto.nome)}"
                        }
                        val mapIntent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(uriString)
                        )
                        context.startActivity(mapIntent)
                    }
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Nome do Posto
                            Text(
                                text = posto.nome,
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Preços
                            if (valorAlcool > 0.0 && valorGasolina > 0.0) {
                                Text(
                                    text = "Álcool: R$ ${posto.precoAlcool} | Gasolina: R$ ${posto.precoGasolina}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                // 👇 2. AQUI ENTRA O RESULTADO NA TELA 👇
                                Text(
                                    text = recomendacao,
                                    style = MaterialTheme.typography.bodyMedium,
                                    // Adicionando um peso extra para o texto ficar em Negrito e chamar a atenção
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary, // Usa a cor principal do seu app
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            // Data
                            Text(
                                text = "Atualizado em: $dataFormatada",
                                style = MaterialTheme.typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.Gray,
                                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                            )

                            // Aviso do Mapa
                            if (posto.coordenadas != null) {
                                Text(
                                    text = "Toque para ver no mapa",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
