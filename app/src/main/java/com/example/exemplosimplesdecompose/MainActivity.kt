package com.example.exemplosimplesdecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.AppOpsManagerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exemplosimplesdecompose.ui.theme.ExemploSimplesDeComposeTheme
import com.example.exemplosimplesdecompose.view.AlcoolGasolinaPreco
import com.example.exemplosimplesdecompose.view.ListaDePostos
import com.example.exemplosimplesdecompose.view.Welcome
import com.google.common.base.Defaults.defaultValue

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExemploSimplesDeComposeTheme {
                val navController: NavHostController = rememberNavController()
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") { Welcome(navController) }
                    composable("mainalcgas") { AlcoolGasolinaPreco(navController) }

                    composable("ListaDePostos/{nomeDoPosto}") { backStackEntry ->
                        // Pega o nome do posto que foi passado na rota, ou deixa vazio se não vier nada
                        val nome = backStackEntry.arguments?.getString("nomeDoPosto") ?: ""
                        ListaDePostos(navController, nome)

                }
                    composable(
                        route = "mainalcgas?nomePosto={nomePosto}", // O '?' torna o parâmetro opcional
                        arguments = listOf(navArgument("nomePosto") {
                            nullable = true
                            defaultValue = null
                        })
                    ) { backStackEntry ->
                        val nomePosto = backStackEntry.arguments?.getString("nomePosto")
                        AlcoolGasolinaPreco(navController, nomePosto) // Passamos o nome para a tela
                    }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExemploSimplesDeComposeTheme {
        Greeting("Android")
    }
}
    }