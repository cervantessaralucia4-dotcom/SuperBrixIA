package com.superbrix.ia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.superbrix.ia.ui.screens.AnalizandoScreen
import com.superbrix.ia.ui.screens.ConfiguracionScreen
import com.superbrix.ia.ui.screens.DetalleEventoScreen
import com.superbrix.ia.ui.screens.HistorialScreen
import com.superbrix.ia.ui.screens.HomeScreen
import com.superbrix.ia.ui.screens.LoginScreen
import com.superbrix.ia.ui.screens.PerfilScreen
import com.superbrix.ia.ui.screens.ReporteNovedadScreen
import com.superbrix.ia.ui.screens.ResultadoReporteScreen
import com.superbrix.ia.ui.screens.SeleccionOperacionScreen
import com.superbrix.ia.ui.screens.SplashScreen
import com.superbrix.ia.viewmodel.EventoViewModel
import com.superbrix.ia.viewmodel.HomeViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel,
    eventoViewModel: EventoViewModel
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val eventoUiState by eventoViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        // 1. Splash Screen
        composable(NavRoutes.SPLASH) {
            SplashScreen(
                onTimeout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // 2. Login Screen
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 3. Home Screen
        composable(NavRoutes.HOME) {
            HomeScreen(
                homeUiState = homeUiState,
                onStartLaborClick = { navController.navigate(NavRoutes.SELECCION_OPERACION) },
                onQuickStartClick = { homeViewModel.startQuickScheduledLabor() },
                onReportInterruptionClick = { navController.navigate(NavRoutes.REPORTE_NOVEDAD) },
                onResumeLaborClick = { homeViewModel.resumeLabor() },
                onFinishLaborClick = { homeViewModel.finishLabor() },
                onHistoryClick = { navController.navigate(NavRoutes.HISTORIAL) },
                onProfileClick = { navController.navigate(NavRoutes.PERFIL) }
            )
        }

        // 4. Seleccion Operacion Screen
        composable(NavRoutes.SELECCION_OPERACION) {
            SeleccionOperacionScreen(
                initialOrder = homeUiState.ordenActiva.codigo,
                initialMachine = homeUiState.maquinaActiva.nombre,
                initialOperation = homeUiState.tipoOperacionActiva,
                onConfirmStartLabor = { orderId, machine, operationType ->
                    homeViewModel.startLabor(orderId, machine, operationType)
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 5. Reporte Novedad Screen
        composable(NavRoutes.REPORTE_NOVEDAD) {
            ReporteNovedadScreen(
                ordenCodigo = homeUiState.ordenActiva.codigo,
                maquinaNombre = homeUiState.maquinaActiva.nombre,
                onSubmitReport = { descripcionText ->
                    homeViewModel.markInterruption()
                    eventoViewModel.reportarNovedad(
                        descripcionText = descripcionText,
                        ordenCodigo = homeUiState.ordenActiva.codigo,
                        maquinaNombre = homeUiState.maquinaActiva.nombre
                    ) {
                        // Callback when analysis finishes
                    }
                    navController.navigate(NavRoutes.ANALIZANDO)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 6. Analizando Screen
        composable(NavRoutes.ANALIZANDO) {
            LaunchedEffect(eventoUiState.isAnalyzing) {
                if (!eventoUiState.isAnalyzing && eventoUiState.ultimoEventoReportado != null) {
                    navController.navigate(NavRoutes.RESULTADO_REPORTE) {
                        popUpTo(NavRoutes.ANALIZANDO) { inclusive = true }
                    }
                }
            }
            AnalizandoScreen()
        }

        // 7. Resultado Reporte Screen
        composable(NavRoutes.RESULTADO_REPORTE) {
            ResultadoReporteScreen(
                evento = eventoUiState.ultimoEventoReportado,
                onBackHomeClick = {
                    navController.popBackStack(NavRoutes.HOME, inclusive = false)
                },
                onViewHistoryClick = {
                    navController.navigate(NavRoutes.HISTORIAL) {
                        popUpTo(NavRoutes.HOME) { inclusive = false }
                    }
                }
            )
        }

        // 8. Historial Screen
        composable(NavRoutes.HISTORIAL) {
            HistorialScreen(
                eventosList = eventoUiState.eventosList,
                onEventClick = { evento ->
                    eventoViewModel.seleccionarEvento(evento)
                    navController.navigate(NavRoutes.DETALLE_EVENTO)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 9. Detalle Evento Screen
        composable(NavRoutes.DETALLE_EVENTO) {
            DetalleEventoScreen(
                evento = eventoUiState.eventoSeleccionado,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 10. Perfil Screen
        composable(NavRoutes.PERFIL) {
            PerfilScreen(
                operario = homeUiState.operario,
                onSettingsClick = { navController.navigate(NavRoutes.CONFIGURACION) },
                onLogoutClick = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.HOME) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 11. Configuracion Screen
        composable(NavRoutes.CONFIGURACION) {
            ConfiguracionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
