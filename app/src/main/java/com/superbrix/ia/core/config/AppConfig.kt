package com.superbrix.ia.core.config

import com.superbrix.ia.BuildConfig

/**
 * Configuración global de la aplicación BRIX360.
 *
 * Centraliza las rutas y URLs de servicios para evitar cadenas hardcodeadas
 * en pantallas y ViewModels. Los valores se configuran a través de BuildConfig / Gradle.
 */
object AppConfig {
    val BASE_URL: String = BuildConfig.BASE_URL
    val APPS_SCRIPT_URL: String = BuildConfig.APPS_SCRIPT_URL
    val SHEETS_URL: String = BuildConfig.SHEETS_URL
    val LOOKER_STUDIO_URL: String = BuildConfig.LOOKER_STUDIO_URL
    val GEMINI_MODEL: String = BuildConfig.GEMINI_MODEL

    const val APP_VERSION_NAME: String = BuildConfig.VERSION_NAME
    const val APP_VERSION_CODE: Int = BuildConfig.VERSION_CODE
}
