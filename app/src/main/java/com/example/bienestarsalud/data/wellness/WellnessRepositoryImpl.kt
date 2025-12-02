package com.example.bienestarsalud.data.wellness

import com.example.bienestarsalud.domain.model.activity.ActivityLog
import com.example.bienestarsalud.domain.model.goals.UserGoals
import com.example.bienestarsalud.domain.model.medical.MedicalProfile
import com.example.bienestarsalud.domain.model.reminder.Reminder
import com.example.bienestarsalud.domain.model.wellness.WellnessRecord
import com.example.bienestarsalud.domain.repository.wellness.WellnessRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import com.example.bienestarsalud.domain.model.gamification.UserStats
import java.util.concurrent.TimeUnit

class WellnessRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : WellnessRepository {

    private fun getTodayDate(): String {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
    }

    // --- DIARIO ---
    override suspend fun saveRecord(record: WellnessRecord) {
        val uid = auth.currentUser?.uid ?: return
        val finalRecord = record.copy(date = getTodayDate())
        firestore.collection("users").document(uid)
            .collection("daily_records").document(finalRecord.date)
            .set(finalRecord).await()
             updateUserStats()
    }

    override suspend fun getTodayRecord(): WellnessRecord? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            firestore.collection("users").document(uid)
                .collection("daily_records").document(getTodayDate())
                .get().await().toObject(WellnessRecord::class.java)
        } catch (e: Exception) { null }
    }

    override suspend fun getAllRecords(): List<WellnessRecord> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            firestore.collection("users").document(uid)
                .collection("daily_records").orderBy("date", Query.Direction.DESCENDING)
                .get().await().toObjects(WellnessRecord::class.java)
        } catch (e: Exception) { emptyList() }
    }

    // --- MÉDICO ---
    override suspend fun saveMedicalProfile(profile: MedicalProfile) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("profile").document("medical_data")
            .set(profile).await()
    }

    override suspend fun getMedicalProfile(): MedicalProfile? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            firestore.collection("users").document(uid)
                .collection("profile").document("medical_data")
                .get().await().toObject(MedicalProfile::class.java)
        } catch (e: Exception) { null }
    }

    // --- ACTIVIDADES ---
    override suspend fun saveActivity(activity: ActivityLog) {
        val uid = auth.currentUser?.uid ?: return
        val id = if (activity.id.isEmpty()) UUID.randomUUID().toString() else activity.id
        firestore.collection("users").document(uid)
            .collection("activities").document(id)
            .set(activity.copy(id = id)).await()
    }

    override suspend fun getActivitiesByDate(date: String): List<ActivityLog> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            firestore.collection("users").document(uid)
                .collection("activities").whereEqualTo("date", date)
                .get().await().toObjects(ActivityLog::class.java)
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun deleteActivity(activityId: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("activities").document(activityId)
            .delete().await()
    }

    // --- RECORDATORIOS ---
    override suspend fun saveReminder(reminder: Reminder) {
        val uid = auth.currentUser?.uid ?: return
        val id = if (reminder.id.isEmpty()) UUID.randomUUID().toString() else reminder.id
        firestore.collection("users").document(uid)
            .collection("reminders").document(id)
            .set(reminder.copy(id = id)).await()
    }

    override suspend fun getReminders(): List<Reminder> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            firestore.collection("users").document(uid)
                .collection("reminders").get().await().toObjects(Reminder::class.java)
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun deleteReminder(reminderId: String) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("reminders").document(reminderId).delete().await()
    }

    // ...
    override suspend fun getAllActivities(): List<ActivityLog> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            firestore.collection("users").document(uid)
                .collection("activities")
                // Ordenar por fecha descendente es ideal, pero requiere índice en Firebase.
                // Por ahora lo ordenaremos en el ViewModel para evitar errores de índices.
                .get().await().toObjects(ActivityLog::class.java)
        } catch (e: Exception) { emptyList() }
    }

    // --- METAS ---
    override suspend fun saveUserGoals(goals: UserGoals) {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid)
            .collection("goals").document("weekly_targets")
            .set(goals).await()
    }

    override suspend fun getUserGoals(): UserGoals {
        val uid = auth.currentUser?.uid ?: return UserGoals()
        return try {
            val snapshot = firestore.collection("users").document(uid)
                .collection("goals").document("weekly_targets")
                .get().await()
            snapshot.toObject(UserGoals::class.java) ?: UserGoals()
        } catch (e: Exception) { UserGoals() }
    }

    override suspend fun getUserStats(): UserStats {
        val uid = auth.currentUser?.uid ?: return UserStats()
        return try {
            firestore.collection("users").document(uid)
                .collection("stats").document("gamification")
                .get().await().toObject(UserStats::class.java) ?: UserStats()
        } catch (e: Exception) { UserStats() }
    }

    override suspend fun updateUserStats() {
        val uid = auth.currentUser?.uid ?: return
        val statsRef = firestore.collection("users").document(uid).collection("stats").document("gamification")

        val currentStats = getUserStats()
        val today = getTodayDate() // "yyyy-MM-dd"

        // Si ya registramos hoy, no hacemos nada
        if (currentStats.lastLogDate == today) return

        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        var newStreak = 1

        if (currentStats.lastLogDate.isNotEmpty()) {
            try {
                val lastDate = dateFormat.parse(currentStats.lastLogDate)
                val todayDate = dateFormat.parse(today)

                // Calcular diferencia de días
                val diff = todayDate!!.time - lastDate!!.time
                val daysDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

                newStreak = if (daysDiff == 1L) {
                    // Si fue ayer, aumentamos racha
                    currentStats.currentStreak + 1
                } else {
                    // Si fue hace más de un día, reiniciamos a 1
                    1
                }
            } catch (e: Exception) {
                newStreak = 1
            }
        }

        val newBest = if (newStreak > currentStats.bestStreak) newStreak else currentStats.bestStreak

        val newStats = UserStats(
            currentStreak = newStreak,
            bestStreak = newBest,
            lastLogDate = today
        )

        statsRef.set(newStats).await()
    }
}


