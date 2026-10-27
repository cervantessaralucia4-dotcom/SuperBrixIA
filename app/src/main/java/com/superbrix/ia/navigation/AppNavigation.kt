package com.superbrix.ia.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.superbrix.ia.data.model.Operario
import com.superbrix.ia.data.model.SyncStatus
import com.superbrix.ia.ui.screens.AdminScreen
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

/**
 * Grafo de Navegación de BRIX360.
 * Conduce el flujo operativo industrial completo asegurando limpieza de backstack
 * en cambios de sesión y cierre de operaciones.
 */
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
            val operarios by homeViewModel.operariosRegistrados.collectAsState()
            LoginScreen(
                operarios = operarios,
                onLoginSuccess = { op ->
                    homeViewModel.setOperarioActivo(op)
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onLoginAdmin = {
                    navController.navigate(NavRoutes.ADMIN)
                }
            )
        }

        // Admin Screen
        composable(NavRoutes.ADMIN) {
            val operarios by homeViewModel.operariosRegistrados.collectAsState()
            AdminScreen(
                operarios = operarios,
                eventos = eventoUiState.eventosList,
                onAddOperario = { n, c, a -> homeViewModel.agregarOperario(n, c, a) },
                onEditOperario = { id, n, c, a -> homeViewModel.editarOperario(id, n, c, a) },
                onDeleteOperario = { id -> homeViewModel.eliminarOperario(id) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 3. Home Screen
        composable(NavRoutes.HOME) {
            HomeScreen(
                homeUiState = homeUiState,
                onStartLaborClick = { navController.navigate(NavRoutes.SELECCION_OPERACION) },
                onQuickStartClick = {
                    homeViewModel.startQuickScheduledLabor()
                    eventoViewModel.reportarEventoDirecto(
                        ordenCodigo = homeUiState.ordenActiva.codigo,
                        maquinaNombre = homeUiState.maquinaActiva.nombre,
                        tipoEvento = "INICIO_LABOR",
                        descripcion = "Inicio de labor rápido",
                        categoria = "Producción Activa",
                        operarioNombre = homeUiState.operario.nombre
                    )
                },
                onReportInterruptionClick = { navController.navigate(NavRoutes.REPORTE_NOVEDAD) },
                onResumeLaborClick = {
                    homeViewModel.resumeLabor()
                    eventoViewModel.reportarEventoDirecto(
                        ordenCodigo = homeUiState.ordenActiva.codigo,
                        maquinaNombre = homeUiState.maquinaActiva.nombre,
                        tipoEvento = "REANUDACION",
                        descripcion = "Se reanuda labor tras interrupción",
                        categoria = "Producción Activa",
                        operarioNombre = homeUiState.operario.nombre
                    )
                },
                onFinishLaborClick = {
                    homeViewModel.finishLabor()
                    eventoViewModel.reportarEventoDirecto(
                        ordenCodigo = homeUiState.ordenActiva.codigo,
                        maquinaNombre = homeUiState.maquinaActiva.nombre,
                        tipoEvento = "FIN_LABOR",
                        descripcion = "Labor finalizada exitosamente",
                        categoria = "Producción Activa",
                        operarioNombre = homeUiState.operario.nombre
                    )
                },
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
                initialArea = homeUiState.areaActiva,
                onConfirmStartLabor = { orderId, machine, operationType, area ->
                    homeViewModel.startLabor(orderId, machine, operationType, area)
                    eventoViewModel.reportarEventoDirecto(
                        ordenCodigo = orderId,
                        maquinaNombre = machine,
                        tipoEvento = "INICIO_LABOR",
                        descripcion = "Inicio manual: $operationType en $area",
                        categoria = "Producción Activa",
                        operarioNombre = homeUiState.operario.nombre
                    )
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
                onSubmitReport = { descripcionText, fotoUri ->
                    homeViewModel.markInterruption()
                    eventoViewModel.reportarNovedad(
                        descripcionText = descripcionText,
                        ordenCodigo = homeUiState.ordenActiva.codigo,
                        maquinaNombre = homeUiState.maquinaActiva.nombre,
                        operarioNombre = homeUiState.operario.nombre,
                        fotoUri = fotoUri
                    ) {
                        // Navegación gestionada por el observador de estado
                    }
                    navController.navigate(NavRoutes.ANALIZANDO)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 6. Analizando Screen
        composable(NavRoutes.ANALIZANDO) {
            LaunchedEffect(eventoUiState.isAnalyzing, eventoUiState.ultimoEventoReportado) {
                if (!eventoUiState.isAnalyzing && eventoUiState.ultimoEventoReportado != null) {
                    navController.navigate(NavRoutes.RESULTADO_REPORTE) {
                        popUpTo(NavRoutes.ANALIZANDO) { inclusive = true }
                    }
                }
            }
            AnalizandoScreen(
                onRetry = {
                    val ult = eventoUiState.ultimoEventoReportado
                    if (ult != null) {
                        eventoViewModel.reportarNovedad(
                            descripcionText = ult.descripcion,
                            ordenCodigo = ult.ordenProduccion,
                            maquinaNombre = ult.maquina,
                            operarioNombre = ult.operario
                        ) {}
                    }
                },
                onSaveFallback = {
                    navController.navigate(NavRoutes.RESULTADO_REPORTE) {
                        popUpTo(NavRoutes.ANALIZANDO) { inclusive = true }
                    }
                }
            )
        }

        // 7. Resultado Reporte Screen
        composable(NavRoutes.RESULTADO_REPORTE) {
            ResultadoReporteScreen(
                evento = eventoUiState.ultimoEventoReportado,
                onCategoryCorrected = { nuevaCat ->
                    eventoViewModel.corregirCategoriaUltimoEvento(nuevaCat)
                },
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
                onAnularEvento = { id, motivo ->
                    eventoViewModel.anularEvento(id, motivo)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 10. Perfil Screen
        composable(NavRoutes.PERFIL) {
            PerfilScreen(
                operario = homeUiState.operario,
                onSettingsClick = { navController.navigate(NavRoutes.CONFIGURACION) },
                onLogoutClick = {
                    // Limpieza total del back stack al cerrar sesión
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 11. Configuracion Screen
        composable(NavRoutes.CONFIGURACION) {
            val pendingCount = eventoUiState.eventosList.count { it.syncStatus == SyncStatus.PENDIENTE }
            ConfiguracionScreen(
                pendingSyncCount = pendingCount,
                onManualSyncClick = {
                    eventoViewModel.sincronizarPendientes()
                },
                onResetDataClick = {
                    eventoViewModel.limpiarHistorial()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
