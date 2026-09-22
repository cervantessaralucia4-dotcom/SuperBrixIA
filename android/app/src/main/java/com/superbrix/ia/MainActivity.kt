package com.superbrix.ia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.superbrix.ia.navigation.AppNavigation
import com.superbrix.ia.ui.theme.SuperBrixIATheme
import com.superbrix.ia.viewmodel.EventoViewModel
import com.superbrix.ia.viewmodel.HomeViewModel

class MainActivity : ComponentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val eventoViewModel: EventoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperBrixIATheme {
                AppNavigation(
                    homeViewModel = homeViewModel,
                    eventoViewModel = eventoViewModel
                )
            }
        }
    }
}
