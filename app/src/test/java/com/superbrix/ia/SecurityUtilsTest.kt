package com.superbrix.ia

import com.superbrix.ia.core.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias para las funciones criptográficas y de seguridad de BRIX360.
 */
class SecurityUtilsTest {

    @Test
    fun sha256_calculaHashHexadecimalCorrecto() {
        // Hash conocido estándar de "1234"
        val expected1234 = "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"
        val actual1234 = SecurityUtils.sha256("1234")
        assertEquals(expected1234, actual1234)

        // Hash conocido estándar de "admin123"
        val expectedAdmin = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9"
        val actualAdmin = SecurityUtils.sha256("admin123")
        assertEquals(expectedAdmin, actualAdmin)
    }

    @Test
    fun verifyAdminPassword_validaContrasenasCorrectasEInvalidas() {
        // Correctas
        assertTrue(SecurityUtils.verifyAdminPassword("superbrix2026"))
        assertTrue(SecurityUtils.verifyAdminPassword("admin123"))

        // Inválidas
        assertFalse(SecurityUtils.verifyAdminPassword("erronea"))
        assertFalse(SecurityUtils.verifyAdminPassword(""))
        assertFalse(SecurityUtils.verifyAdminPassword("    "))
    }

    @Test
    fun verifySupervisorPin_validaPinCorrectoEInvalido() {
        // Correctos
        assertTrue(SecurityUtils.verifySupervisorPin("1234"))
        assertTrue(SecurityUtils.verifySupervisorPin("superbrix"))

        // Inválidos
        assertFalse(SecurityUtils.verifySupervisorPin("0000"))
        assertFalse(SecurityUtils.verifySupervisorPin("9876"))
        assertFalse(SecurityUtils.verifySupervisorPin(""))
    }
}
