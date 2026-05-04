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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.exemplosimplesdecompose.R
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.model.Posto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDePostos(navController: NavHostController, nomeDoPosto: String) {
    // Contexto necessário para iniciar a Intent do mapa
    val context = LocalContext.current

    // Seus dados mockados
    val postoSP = Posto("model.Posto SP", Coordenadas(41.40338, 2.17403))
    val postoNY = Posto("model.Posto em NY", Coordenadas(40.7128, -74.0060))
    val postoN = Posto(nomeDoPosto) // Se não passamos coordenadas, assumimos que é null ou vazia

    // Esta é a lista que vamos usar de fato
    val postosComp = listOf(postoN, postoSP, postoNY)

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
            items(postosComp) { posto ->
                Card(
                    onClick = {
                        // 2. Lógica para Abrir o Mapa

                        // Cria a URI baseada na existência ou não de coordenadas.
                        // Se não houver coordenadas, o mapa fará uma pesquisa pelo nome do posto.
                        val uriString = if (posto.coordenadas != null) {
                            "geo:${posto.coordenadas.latitude},${posto.coordenadas.longitude}?q=${posto.coordenadas.latitude},${posto.coordenadas.longitude}(${Uri.encode(posto.nome)})"
                        } else {
                            "geo:0,0?q=${Uri.encode(posto.nome)}"
                        }

                        // Cria a "intenção" de visualização
                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))

                        // Opcional: Se quiser forçar abrir no Google Maps em vez de perguntar ao usuário:
                        // mapIntent.setPackage("com.google.android.apps.maps")

                        // Dispara a ação para abrir o mapa
                        context.startActivity(mapIntent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Exibimos o nome do objeto model.Posto
                            Text(
                                text = posto.nome,
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Adicionei um detalhe extra: mostrar se ele tem coordenadas
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