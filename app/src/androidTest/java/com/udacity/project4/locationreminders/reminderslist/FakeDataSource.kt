package com.udacity.project4.locationreminders.reminderslist

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var remindersList: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false

    fun setShouldReturnError(error: Boolean) {
        shouldReturnError = error
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) return Result.Error(("Reminders not found!"))

        remindersList?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(("Reminders not found!"))
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        remindersList?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) return Result.Error(("Reminders not found!"))

        val reminder = remindersList?.find {
            it.id == id
        }

        reminder?.let { return Result.Success(it) }
        return Result.Error(("Reminders not found!"))
    }

    override suspend fun deleteAllReminders() {
        remindersList?.clear()
    }

}