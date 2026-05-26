package com.example.exemplosimplesdecompose.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import com.example.exemplosimplesdecompose.data.Coordenadas
import com.example.exemplosimplesdecompose.data.PostoStorage
import com.example.exemplosimplesdecompose.model.Posto
import com.google.android.gms.location.LocationServices

@Composable
fun AlcoolGasolinaPreco(
    navController: NavHostController,
    nomePostoParaEditar: String? = null
) {

    val context = LocalContext.current

    val sharedPreferences = remember {
        context.getSharedPreferences(
            "ConfiguracoesApp",
            Context.MODE_PRIVATE
        )
    }

    val storage = remember {
        PostoStorage(context)
    }

    var alcool by remember { mutableStateOf("") }

    var gasolina by remember { mutableStateOf("") }

    var nomeDoPosto by remember { mutableStateOf("") }

    var coordenadasCapturadas by remember {
        mutableStateOf<Coordenadas?>(null)
    }

    var localizacaoManual by remember {
        mutableStateOf("")
    }

    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var checkedState by remember {

        mutableStateOf(
            sharedPreferences.getBoolean(
                "estado_switch_75",
                true
            )
        )
    }

    LaunchedEffect(nomePostoParaEditar) {

        if (nomePostoParaEditar != null) {

            val postoExistente =
                storage.buscarPostos().find {
                    it.nome == nomePostoParaEditar
                }

            postoExistente?.let {

                nomeDoPosto = it.nome
                alcool = it.precoAlcool
                gasolina = it.precoGasolina
                localizacaoManual = it.localizacao
                coordenadasCapturadas = it.coordenadas
            }
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

            val coarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {

                try {

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->

                            if (location != null) {

                                coordenadasCapturadas =
                                    Coordenadas(
                                        location.latitude,
                                        location.longitude
                                    )
                            }
                        }

                } catch (e: SecurityException) {

                    e.printStackTrace()
                }
            }
        }

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

            val currentLocales =
                androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()

            val isEnglish =
                currentLocales.toLanguageTags().startsWith("en")

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.End
            ) {

                TextButton(

                    modifier = Modifier.semantics {
                        contentDescription = "Alterar idioma"
                    },

                    onClick = {

                        val targetLang =
                            if (isEnglish) "pt-BR" else "en"

                        val localeList =
                            androidx.core.os.LocaleListCompat
                                .forLanguageTags(targetLang)

                        androidx.appcompat.app.AppCompatDelegate
                            .setApplicationLocales(localeList)
                    }

                ) {

                    Text(
                        if (isEnglish) "🇧🇷 PT" else "🇺🇸 EN"
                    )
                }
            }

            OutlinedTextField(
                value = alcool,

                onValueChange = {
                    alcool = it
                },

                label = {
                    Text(stringResource(id = R.string.preco_alcool))
                },

                modifier = Modifier.fillMaxWidth(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = gasolina,

                onValueChange = {
                    gasolina = it
                },

                label = {
                    Text(stringResource(id = R.string.preco_gasolina))
                },

                modifier = Modifier.fillMaxWidth(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = nomeDoPosto,

                onValueChange = {
                    nomeDoPosto = it
                },

                label = {
                    Text(stringResource(id = R.string.nome_posto))
                },

                modifier = Modifier.fillMaxWidth(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            OutlinedTextField(
                value = localizacaoManual,

                onValueChange = {
                    localizacaoManual = it
                },

                label = {
                    Text(stringResource(id = R.string.digite_localização))
                },

                modifier = Modifier.fillMaxWidth(),

                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                horizontalArrangement = Arrangement.Start
            ) {

                Text(
                    text = "75%",

                    style = MaterialTheme.typography.bodyLarge,

                    color = MaterialTheme.colorScheme.onBackground,

                    modifier = Modifier.padding(
                        top = 16.dp,
                        end = 16.dp
                    )
                )

                Switch(

                    modifier = Modifier.semantics {
                        contentDescription = "Configuração de 75%"
                    },

                    checked = checkedState,

                    onCheckedChange = { novoEstado ->

                        checkedState = novoEstado

                        sharedPreferences.edit()
                            .putBoolean(
                                "estado_switch_75",
                                novoEstado
                            )
                            .apply()
                    },

                    thumbContent = {

                        if (checkedState) {

                            Icon(
                                imageVector = Icons.Filled.Check,

                                contentDescription = "Ativado",

                                modifier = Modifier.size(
                                    SwitchDefaults.IconSize
                                )
                            )
                        }
                    }
                )
            }

            val valorAlcool =
                alcool.toDoubleOrNull() ?: 0.0

            val valorGasolina =
                gasolina.toDoubleOrNull() ?: 0.0

            val taxaRendimento =
                if (checkedState) 0.75 else 0.70

            val resultado =
                if (valorAlcool > 0.0 && valorGasolina > 0.0) {

                    if (valorAlcool <= (valorGasolina * taxaRendimento)) {

                        stringResource(id = R.string.sugestao_alcool2)

                    } else {

                        stringResource(id = R.string.sugestao_gasolina2)
                    }

                } else {

                    stringResource(id = R.string.digitar_valores)
                }

            Text(
                text = resultado,

                style = MaterialTheme.typography.titleMedium,

                color = MaterialTheme.colorScheme.onBackground,

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),

                textAlign = TextAlign.Center
            )

            Button(

                onClick = {

                    val hasFineLocation =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED

                    if (hasFineLocation) {

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location: Location? ->

                                if (location != null) {

                                    coordenadasCapturadas =
                                        Coordenadas(
                                            location.latitude,
                                            location.longitude
                                        )
                                }
                            }

                    } else {

                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },

                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {

                Icon(
                    Icons.Filled.LocationOn,

                    contentDescription = "Capturar localização"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                if (coordenadasCapturadas != null) {

                    Text(
                        text = stringResource(
                            id = R.string.localizacao_capturada
                        )
                    )

                } else {

                    Text(
                        text = stringResource(
                            id = R.string.pegar_localizacao
                        )
                    )
                }
            }

            Button(

                onClick = {

                    if (
                        nomeDoPosto.isNotEmpty() &&
                        alcool.isNotEmpty() &&
                        gasolina.isNotEmpty()
                    ) {

                        val novoPosto = Posto(
                            nome = nomeDoPosto,
                            precoAlcool = alcool,
                            precoGasolina = gasolina,
                            localizacao = localizacaoManual,
                            coordenadas = coordenadasCapturadas
                        )

                        if (nomePostoParaEditar != null) {

                            storage.atualizarPosto(
                                nomePostoParaEditar,
                                novoPosto
                            )

                        } else {

                            storage.adicionarPosto(novoPosto)
                        }

                        navController.navigate(
                            "ListaDePostos/$nomeDoPosto"
                        )
                    }
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    if (nomePostoParaEditar != null)
                        "Atualizar Posto"
                    else
                        stringResource(id = R.string.salvar_posto)
                )
            }

            OutlinedButton(

                onClick = {
                    navController.navigate("ListaDePostos/todos")
                },

                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Filled.List,

                    contentDescription = "Abrir lista de postos"
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = stringResource(
                        id = R.string.lista_postos
                    )
                )
            }
        }
    }
}