package com.example.bienestarsalud.domain.repository.wellness

import com.example.bienestarsalud.domain.model.activity.ActivityLog
import com.example.bienestarsalud.domain.model.goals.UserGoals
import com.example.bienestarsalud.domain.model.medical.MedicalProfile
import com.example.bienestarsalud.domain.model.reminder.Reminder
import com.example.bienestarsalud.domain.model.wellness.WellnessRecord

interface WellnessRepository {
    // Diario
    suspend fun saveRecord(record: WellnessRecord)
    suspend fun getTodayRecord(): WellnessRecord?
    suspend fun getAllRecords(): List<WellnessRecord>

    // Médico
    suspend fun saveMedicalProfile(profile: MedicalProfile)
    suspend fun getMedicalProfile(): MedicalProfile?

    // Actividades
    suspend fun saveActivity(activity: ActivityLog)
    suspend fun getActivitiesByDate(date: String): List<ActivityLog>
    suspend fun deleteActivity(activityId: String)

    // Recordatorios
    suspend fun saveReminder(reminder: Reminder)
    suspend fun getReminders(): List<Reminder>
    suspend fun deleteReminder(reminderId: String)
    suspend fun getAllActivities(): List<ActivityLog>

    //goals
    suspend fun saveUserGoals(goals: UserGoals)
    suspend fun getUserGoals(): UserGoals

    suspend fun updateUserStats() // Función para recalcular racha
    suspend fun getUserStats(): com.example.bienestarsalud.domain.model.gamification.UserStats
}