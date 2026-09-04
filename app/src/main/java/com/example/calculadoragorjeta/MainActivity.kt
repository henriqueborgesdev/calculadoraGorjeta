package com.example.calculadoragorjeta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.calculadoragorjeta.navigation.CalculadoraGorjeta
import com.example.calculadoragorjeta.navigation.Menu
import com.example.calculadoragorjeta.ui.screens.MenuScreen
import com.example.calculadoragorjeta.ui.screens.GorjetaScreen
import com.example.calculadoragorjeta.ui.theme.CalculadoraGorjetaTheme

/**
 * Activity única do projeto. Hospeda um NavHost com dois destinos: o menu
 * inicial e a tela da Calculadora de Gorjeta.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ajusta as cores dos ícones das barras do sistema ao tema claro/escuro.
        enableEdgeToEdge()
        setContent {
            CalculadoraGorjetaTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        // Mantém o conteúdo fora da barra de status e da barra
                        // de navegação do sistema.
                        .safeDrawingPadding()
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Menu) {
                        composable<Menu> {
                            MenuScreen(
                                onAbrirCalculadoraGorjeta = {
                                    navController.navigate(CalculadoraGorjeta)
                                }
                            )
                        }
                        composable<CalculadoraGorjeta> {
                            GorjetaScreen(onVoltar = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
