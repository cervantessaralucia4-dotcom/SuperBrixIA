package com.superbrix.ia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
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
