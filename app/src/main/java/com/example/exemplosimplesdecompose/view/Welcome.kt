package com.example.exemplosimplesdecompose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.exemplosimplesdecompose.R
import kotlinx.coroutines.delay

@Composable
fun Welcome(navController: NavHostController) {

    // ⏲️ Lógica do Timer: Executa apenas uma vez quando a tela abre
    LaunchedEffect(Unit) {
        delay(2000L) // Espera 2000 milissegundos (2 segundos)

        // Pula para a próxima tela
        navController.navigate("mainalcgas") {
            // Isso aqui é importante: remove a tela de welcome do "histórico".
            // Assim, se o usuário apertar "voltar", o app fecha em vez de voltar pro Welcome.
            popUpTo("welcome") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. A Foto Inicial (Altere o nome 'logo_app' para o nome da sua foto no drawable)
        Image(
            painter = painterResource(id = R.drawable.posto_de_gasolina),
            contentDescription = "Logo do Posto",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. A Mensagem de Boas-Vindas personalizada
        Text(
            text = stringResource(id = R.string.al_gas), // Altere sua mensagem aqui
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(id = R.string.boas_vindas),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}