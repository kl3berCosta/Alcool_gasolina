package com.example.exemplosimplesdecompose.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    var listaDePostos by remember { mutableStateOf(storage.buscarPostos()) }

    val prefs = context.getSharedPreferences("PostosPrefs", Context.MODE_PRIVATE)
    val usar75 = prefs.getBoolean("estado_switch_75", false)
    val taxaRendimento = if (usar75) 0.75 else 0.70

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.lista_postos)) })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(listaDePostos) { posto ->
                val valorAlcool = posto.precoAlcool.toDoubleOrNull() ?: 0.0
                val valorGasolina = posto.precoGasolina.toDoubleOrNull() ?: 0.0

                val recomendacao = if (valorAlcool > 0.0 && valorGasolina > 0.0) {
                    if (valorAlcool <= (valorGasolina * taxaRendimento)) {
                        stringResource(id = R.string.sugestao_alcool)
                    } else {
                        stringResource(id = R.string.sugestao_gasolina)
                    }
                } else ""

                val formatoData = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                val dataFormatada = formatoData.format(java.util.Date(posto.dataCadastro))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        Text(text = posto.nome, style = MaterialTheme.typography.titleMedium)

                        if (valorAlcool > 0.0 && valorGasolina > 0.0) {
                            Text(
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

                        Text(
                            text = stringResource(id = R.string.atualizado_em, dataFormatada),
                            style = MaterialTheme.typography.labelSmall,
                            color = androidx.compose.ui.graphics.Color.Gray,
                            modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                        )

                        if (posto.localizacao.isNotEmpty()) {
                            Text(
                                text = "📍 ${posto.localizacao}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            TextButton(onClick = {
                                val uriString = if (posto.coordenadas != null) {
                                    "geo:${posto.coordenadas.latitude},${posto.coordenadas.longitude}?q=${posto.coordenadas.latitude},${posto.coordenadas.longitude}(${Uri.encode(posto.nome)})"
                                } else {
                                    "geo:0,0?q=${Uri.encode(posto.nome)}"
                                }
                                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                                context.startActivity(mapIntent)
                            }) {
                                Icon(Icons.Filled.Place, contentDescription = "Mapa", modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(text = stringResource(id = R.string.Mapa))
                            }

                            Row {
                                IconButton(onClick = {
                                    navController.navigate("mainalcgas?nomePosto=${posto.nome}")
                                }) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Editar")
                                }

                                IconButton(onClick = {
                                    storage.excluirPosto(posto.nome)
                                    listaDePostos = storage.buscarPostos()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Excluir",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}