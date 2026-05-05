package com.example.exemplosimplesdecompose.view

import android.content.Context
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.PostoStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDePostos(navController: NavHostController, nomeDoPosto: String) {
    val context = LocalContext.current
    val storage = remember { PostoStorage(context) }
    val listaDePostos = storage.buscarPostos()

    // 🚀 OTIMIZAÇÃO: Lemos a configuração dos 75% UMA ÚNICA VEZ antes de montar a lista
    val prefs = context.getSharedPreferences("PostosPrefs", Context.MODE_PRIVATE)
    val usar75 = prefs.getBoolean("estado_switch_75", false)
    val taxaRendimento = if (usar75) 0.75 else 0.70

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
            items(listaDePostos) { posto ->
                // --- MATEMÁTICA AUTOMÁTICA ---
                val valorAlcool = posto.precoAlcool.toDoubleOrNull() ?: 0.0
                val valorGasolina = posto.precoGasolina.toDoubleOrNull() ?: 0.0

                val recomendacao = if (valorAlcool > 0.0 && valorGasolina > 0.0) {
                    if (valorAlcool <= (valorGasolina * taxaRendimento)) {
                        stringResource(id = R.string.sugestao_alcool)
                    } else {
                        stringResource(id = R.string.sugestao_gasolina)
                    }
                } else {
                    ""
                }

                // Formatação da Data
                val formatoData = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                val dataFormatada = formatoData.format(java.util.Date(posto.dataCadastro))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    onClick = {
                        val uriString = if (posto.coordenadas != null) {
                            "geo:${posto.coordenadas.latitude},${posto.coordenadas.longitude}?q=${posto.coordenadas.latitude},${posto.coordenadas.longitude}(${Uri.encode(posto.nome)})"
                        } else {
                            "geo:0,0?q=${Uri.encode(posto.nome)}"
                        }
                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
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

                            // Preços e Recomendação
                            if (valorAlcool > 0.0 && valorGasolina > 0.0) {
                                Text(
                                    // Injetando as variáveis de preço dentro da string!
                                    text = stringResource(id = R.string.precos_lista, posto.precoAlcool, posto.precoGasolina),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Text(
                                    text = recomendacao,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            // Data Atualizada
                            Text(
                                // Injetando a data formatada dentro da string!
                                text = stringResource(id = R.string.atualizado_em, dataFormatada),
                                style = MaterialTheme.typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.Gray,
                                modifier = Modifier.padding(top = 2.dp, bottom = 4.dp)
                            )

                            // Aviso do Mapa
                            if (posto.coordenadas != null) {
                                Text(
                                    text = stringResource(id = R.string.toque_mapa),
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