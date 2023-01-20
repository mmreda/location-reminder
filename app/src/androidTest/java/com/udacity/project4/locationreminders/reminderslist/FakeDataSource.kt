package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource : ReminderDataSource {

    private var remindersFakeData:LinkedHashMap<String, ReminderDTO> = LinkedHashMap()

    private var shouldReturnError = false

    fun setShouldReturnError(error: Boolean) {
        shouldReturnError = error
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError) {
            Result.Error(("Reminders not found!"))
        } else {
            Result.Success(remindersFakeData.values.toList())
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersFakeData[reminder.id] = reminder
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return if (shouldReturnError) {
            Result.Error(("Reminder not found!"))
        } else {
            Result.Success(remindersFakeData.getValue(id))
        }
    }

    override suspend fun deleteAllReminders() {
        remindersFakeData.clear()
    }

}