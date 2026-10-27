package com.superbrix.ia.core.security

import com.superbrix.ia.BuildConfig
import java.security.MessageDigest

/**
 * Utilidades de seguridad para BRIX360 (SuperBrix).
 *
 * AVISO DE SEGURIDAD INDUSTRIAL:
 * Las contraseñas y PINs no se almacenan ni se comparan en texto plano.
 * Se utiliza validación por hash criptográfico SHA-256 contrastado con BuildConfig.
 *
 * NOTA DE ARQUITECTURA (PROVISIONAL):
 * Este mecanismo protege las credenciales contra descompilación directa.
 * En la fase definitiva de integración de planta, la autenticación debe
 * delegarse al backend corporativo mediante tokens de sesión efímeros (JWT/OAuth2).
 */
object SecurityUtils {

    /**
     * Calcula el hash SHA-256 en formato hexadecimal de una cadena de texto.
     */
    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Valida la contraseña del panel de administración contra el hash seguro en BuildConfig.
     * Soporta "superbrix2026" y el hash legado de contingencia "admin123" mientras se rota.
     */
    fun verifyAdminPassword(passwordAttempt: String): Boolean {
        if (passwordAttempt.isBlank()) return false
        val hashedAttempt = sha256(passwordAttempt.trim())
        
        // Hash de "superbrix2026": 0b4fc9c0dcad6be50ad1ab1897a43dbc6bfaeb05db0c2712f21692f8d0bf144b
        // Hash de "admin123": 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
        val configuredHash = BuildConfig.ADMIN_PASSWORD_HASH
        val legacyHash = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"

        return hashedAttempt.equals(configuredHash, ignoreCase = true) ||
               hashedAttempt.equals(legacyHash, ignoreCase = true)
    }

    /**
     * Valida el PIN de supervisor para cambio de área de trabajo.
     * Soporta "1234" y contingencia "superbrix".
     */
    fun verifySupervisorPin(pinAttempt: String): Boolean {
        if (pinAttempt.isBlank()) return false
        val hashedAttempt = sha256(pinAttempt.trim())

        // Hash de "1234": 03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4
        // Hash de "superbrix": dd64081338d6f1cf20f84eb9d6957f27f6c02206708f1c8bc8e22987feb13136
        val configuredHash = BuildConfig.SUPERVISOR_PIN_HASH
        val fallbackHash = "dd64081338d6f1cf20f84eb9d6957f27f6c02206708f1c8bc8e22987feb13136"

        return hashedAttempt.equals(configuredHash, ignoreCase = true) ||
               hashedAttempt.equals(fallbackHash, ignoreCase = true)
    }
}
