package com.example.exemplosimplesdecompose.view

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.android.gms.location.LocationServices

// Lembre-se de verificar se estes 4 pacotes abaixo batem com o nome do seu projeto:
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.data.PostoStorage
import com.example.exemplosimplesdecompose.model.Posto
@Composable
fun AlcoolGasolinaPreco(navController: NavHostController) {
    // 1. Pegamos o contexto atual para acessar o SharedPreferences
    val context = LocalContext.current
    val sharedPreferences = remember {
        context.getSharedPreferences("ConfiguracoesApp", Context.MODE_PRIVATE)
    }
    val storage = remember { PostoStorage(context) }
    var alcool by remember { mutableStateOf("") }
    var gasolina by remember { mutableStateOf("") }
    var nomeDoPosto by remember { mutableStateOf("") }
    var coordenadasCapturadas by remember { mutableStateOf<Coordenadas?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // 2. Lemos o valor salvo ao criar a tela. O padrão é 'true' caso não exista.
    var checkedState by remember {
        mutableStateOf(sharedPreferences.getBoolean("estado_switch_75", true))
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            // Permissão concedida! Vamos pegar a localização
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        coordenadasCapturadas = Coordenadas(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    // A surface container using the 'background' color from the theme
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🌐 Lógica do Idioma
            val currentLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
            val isEnglish = currentLocales.toLanguageTags().startsWith("en")

            // Botão posicionado no canto superior direito
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                androidx.compose.material3.TextButton(
                    onClick = {
                        val targetLang = if (isEnglish) "pt-BR" else "en"
                        val localeList = androidx.core.os.LocaleListCompat.forLanguageTags(targetLang)
                        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(localeList)
                    }
                ) {
                    Text(if (isEnglish) "🇧🇷 PT" else "🇺🇸 EN")
                }
            }

            // 👇 AQUI ABAIXO CONTINUAM OS SEUS CAMPOS DE TEXTO NORMAIS 👇
            // OutlinedTextField( value = alcool ... )
            // Campo de texto para entrada do preço
            OutlinedTextField(
                value = alcool,
                onValueChange = { alcool = it },
                label = { Text(stringResource(id = R.string.preco_alcool)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            // Campo de texto para preço da Gasolina
            OutlinedTextField(
                value = gasolina,
                onValueChange = { gasolina = it },
                label = { Text(stringResource(id = R.string.preco_gasolina)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            // Campo de texto para NOME do model.Posto (Corrigido para KeyboardType.Text)
            OutlinedTextField(
                value = nomeDoPosto,
                onValueChange = { nomeDoPosto = it },
                label = { Text(stringResource(id = R.string.nome_posto)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                horizontalArrangement = Arrangement.Start) {
                Text(
                    text = "75%",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp) // Adicionado 'end' para desgrudar do Switch
                )
                Switch(
                    modifier = Modifier.semantics { contentDescription = "Configuração de 75%" },
                    checked = checkedState,
                    onCheckedChange = { novoEstado ->
                        // 3. Atualizamos a interface
                        checkedState = novoEstado

                        // 4. Salvamos o novo estado imediatamente no SharedPreferences
                        sharedPreferences.edit()
                            .putBoolean("estado_switch_75", novoEstado)
                            .apply()
                    },
                    thumbContent = {
                        if (checkedState) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                )
            }
            // --- CÁLCULO AUTOMÁTICO ---
// Transforma o texto em número (se estiver vazio ou inválido, vira 0.0)
            val valorAlcool = alcool.toDoubleOrNull() ?: 0.0
            val valorGasolina = gasolina.toDoubleOrNull() ?: 0.0

// Aqui está a mágica conectada ao seu Switch!
            val taxaRendimento = if (checkedState) 0.75 else 0.70

// Faz a conta
            val resultado = if (valorAlcool > 0.0 && valorGasolina > 0.0) {
                // Se o preço do álcool for menor ou igual à (gasolina * taxa)
                if (valorAlcool <= (valorGasolina * taxaRendimento)) {
                    stringResource(id = R.string.sugestao_alcool2)
                } else {
                    stringResource(id = R.string.sugestao_gasolina2)
                }
            } else {
                stringResource(id = R.string.digitar_valores)
            }

// Mostra a resposta na tela
            Text(
                text = resultado,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            // 2. Botão para capturar localização
            Button(
                onClick = {
                    // Verifica se já temos permissão
                    val hasFineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                    if (hasFineLocation) {
                        // Se já tem, pega direto
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            if (location != null) {
                                coordenadasCapturadas = Coordenadas(location.latitude, location.longitude)
                            }
                        }
                    } else {
                        // Se não tem, pede a permissão
                        permissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                if (coordenadasCapturadas != null) {
                    Text(text = stringResource(id = R.string.localizacao_capturada))
                } else {
                    Text(text = stringResource(id = R.string.pegar_localizacao))
                }
            }
            // Botão de cálculo
            Button(
                onClick = {
                    if (nomeDoPosto.isNotEmpty() && alcool.isNotEmpty() && gasolina.isNotEmpty()) {
                        val novoPosto = Posto(
                            nome = nomeDoPosto,
                            precoAlcool = alcool,
                            precoGasolina = gasolina,
                            localizacao = "Endereço opcional ou automático",
                            coordenadas = coordenadasCapturadas // <-- Passamos a coordenada aqui!
                        )
                        storage.adicionarPosto(novoPosto)
                        navController.navigate("ListaDePostos/$nomeDoPosto")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.salvar_posto))
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Botão exclusivo para ir para a lista sem salvar nada
            OutlinedButton(
                onClick = {
                    // Passamos a palavra "todos" só para preencher a exigência da rota
                    navController.navigate("ListaDePostos/todos")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.List, contentDescription = "Ver lista")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.lista_postos))
            }
        }


        }
    }
