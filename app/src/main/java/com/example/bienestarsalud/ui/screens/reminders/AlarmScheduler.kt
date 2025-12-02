package com.example.bienestarsalud.ui.screens.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.bienestarsalud.domain.model.reminder.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class AlarmScheduler @Inject constructor(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(reminder: Reminder) {
        if (!reminder.isEnabled) return

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TITLE", reminder.title)
            putExtra("MESSAGE", "¡Es hora! ${reminder.title}")
            // Pasamos el intervalo para que el Receiver sepa si debe reprogramar
            putExtra("INTERVAL", reminder.repeatIntervalHours)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeInMillis = calculateNextTriggerTime(reminder)
        if (timeInMillis == -1L) return

        try {
            // Si tiene intervalo (ej: agua cada 2h), usamos setRepeating (inexacto para ahorrar batería)
            // Si es puntual, usamos setExact
            if (reminder.repeatIntervalHours > 0) {
                val intervalMillis = reminder.repeatIntervalHours * 60 * 60 * 1000L
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    intervalMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            }
            Log.d("AlarmScheduler", "Alarma '${reminder.title}' programada para: ${java.util.Date(timeInMillis)}")
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancel(reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    // --- MAGIA: Calcular el próximo día válido ---
    private fun calculateNextTriggerTime(reminder: Reminder): Long {
        return try {
            val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val date = sdf.parse(reminder.time) ?: return -1

            val targetCal = Calendar.getInstance().apply {
                time = date
            }

            // Configuramos la hora en un calendario actual
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, targetCal.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, targetCal.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val now = Calendar.getInstance()

            // Si la hora ya pasó hoy, empezamos a buscar desde mañana
            if (calendar.before(now)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            // Buscamos el siguiente día que coincida con los seleccionados
            // (Máximo 7 días de búsqueda para evitar bucles infinitos)
            for (i in 0..7) {
                val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 1=Domingo, 2=Lunes...

                if (reminder.daysOfWeek.contains(currentDayOfWeek)) {
                    // ¡Encontramos el día!
                    return calendar.timeInMillis
                }
                // Si no es hoy, probamos mañana
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            -1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}