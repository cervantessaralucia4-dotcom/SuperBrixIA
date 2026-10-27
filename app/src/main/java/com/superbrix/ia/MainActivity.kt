package com.superbrix.ia

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.fragment.app.FragmentActivity
import com.superbrix.ia.core.notifications.PlantNotificationHelper
import com.superbrix.ia.data.local.PreferencesManager
import com.superbrix.ia.navigation.AppNavigation
import com.superbrix.ia.ui.theme.SuperBrixIATheme
import com.superbrix.ia.viewmodel.EventoViewModel
import com.superbrix.ia.viewmodel.HomeViewModel

class MainActivity : FragmentActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val eventoViewModel: EventoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar canal de notificaciones industriales para emergencias y novedades
        PlantNotificationHelper.createNotificationChannel(this)

        setContent {
            val prefsManager = remember { PreferencesManager(applicationContext) }
            val isDarkTheme by prefsManager.isDarkModeFlow.collectAsState(initial = true)

            SuperBrixIATheme(darkTheme = isDarkTheme) {
                AppNavigation(
                    homeViewModel = homeViewModel,
                    eventoViewModel = eventoViewModel
                )
            }
        }
    }
}
