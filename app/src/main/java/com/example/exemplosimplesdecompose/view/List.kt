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
                // Cria um formatador para transformar os milissegundos em uma data legível
                val formatoData =
                    java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                val dataFormatada = formatoData.format(java.util.Date(posto.dataCadastro))

                Card(
                    // Modificador unificado (margens e preenchimento corretos)
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    onClick = {
                        // Lógica para Abrir o Mapa (Mantida intacta)
                        val uriString = if (posto.coordenadas != null) {
                            "geo:${posto.coordenadas.latitude},${posto.coordenadas.longitude}?q=${posto.coordenadas.latitude},${posto.coordenadas.longitude}(${
                                Uri.encode(
                                    posto.nome
                                )
                            })"
                        } else {
                            "geo:0,0?q=${Uri.encode(posto.nome)}"
                        }

                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                        context.startActivity(mapIntent)
                    }
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 1. Nome do Posto
                            Text(
                                text = posto.nome,
                                style = MaterialTheme.typography.titleMedium
                            )

                            // 2. Preços gravados dos combustíveis
                            if (posto.precoAlcool.isNotBlank() && posto.precoGasolina.isNotBlank()) {
                                Text(
                                    text = "Álcool: R$ ${posto.precoAlcool} | Gasolina: R$ ${posto.precoGasolina}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            // 3. Data da informação
                            Text(
                                text = "Atualizado em: $dataFormatada",
                                style = MaterialTheme.typography.labelSmall,
                                color = androidx.compose.ui.graphics.Color.Gray,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )

                            // 4. Localização no mapa (Com internacionalização stringResource)
                            if (posto.coordenadas != null) {
                                Text(
                                    text = stringResource(id = R.string.toque_mapa),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}