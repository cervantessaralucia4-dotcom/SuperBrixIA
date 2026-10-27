package com.superbrix.ia

import android.app.Application
import com.superbrix.ia.core.notifications.PlantNotificationHelper
import com.superbrix.ia.data.sync.SyncWorker

/**
 * Clase Application principal de BRIX360.
 *
 * Inicializa:
 * - Canal de notificaciones industriales de alta prioridad para seguridad de planta.
 * - Sincronización periódica automática con WorkManager (cada 15 minutos con red).
 */
class SuperBrixApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicializar canal de notificaciones industriales
        PlantNotificationHelper.createNotificationChannel(this)

        // Programar sincronización periódica en segundo plano
        SyncWorker.schedulePeriodicSync(this)
    }
}
