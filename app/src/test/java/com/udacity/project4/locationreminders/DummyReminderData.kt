package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

object DummyReminderData {
    val reminder = ReminderDataItem("Reminder to visit pyramids", "",
        "Pyramids of Giza", 29.991763586846275, 31.133968294733766)

    val reminders = arrayListOf(
        ReminderDTO("Reminder1", "Desc1",
            "Location1", 29.991763586846275, 31.133968294733766),
        ReminderDTO("Reminder2", "Desc2",
            "Location2", 29.976449889966347, 31.01473648790832)
    )
}