package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb() {
        // Using an in-memory database so that the information stored here disappears when the
        // process is killed.
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminder_and_getReminderById() = runBlockingTest {
        //GIVEN - insert a reminder
        val reminder = ReminderDTO("Reminder to visit pyramids", "",
            "Pyramids of Giza", 29.991763586846275, 31.133968294733766)
        database.reminderDao().saveReminder(reminder)

        //WHEN - get a reminder from the database by id
        val result = database.reminderDao().getReminderById(reminder.id)

        //THEN - The loaded data contains the expected values
        assertThat<ReminderDTO>(result as ReminderDTO, notNullValue())
        assertThat(result.id, `is`(reminder.id))
        assertThat(result.title, `is`(reminder.title))
        assertThat(result.location, `is`(reminder.location))
        assertThat(result.latitude, `is`(reminder.latitude))
        assertThat(result.longitude, `is`(reminder.longitude))
    }

    @Test
    fun insertReminders_and_getAllReminders() = runBlockingTest {
        //GIVEN - insert a 2 reminders
        val reminder1 = ReminderDTO("Reminder1", "Desc1", "Location1", 29.991763586846275, 31.133968294733766)
        val reminder2 = ReminderDTO("Reminder2", "Desc2", "Location2", 29.976449889966347, 31.01473648790832)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        //WHEN - get all reminders from the database
        val result = database.reminderDao().getReminders()

        //THEN - The loaded data contains the expected size and id
        assertThat(result.size, `is`(3))
        assertThat(result[0].id, `is`(reminder1.id))
        assertThat(result[1].id, `is`(reminder2.id))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        //GIVEN - insert a 2 reminders
        val reminder1 = ReminderDTO("Reminder1", "Desc1", "Location1", 29.991763586846275, 31.133968294733766)
        val reminder2 = ReminderDTO("Reminder2", "Desc2", "Location2", 29.976449889966347, 31.01473648790832)
        database.reminderDao().saveReminder(reminder1)
        database.reminderDao().saveReminder(reminder2)

        //WHEN - delete all reminders
        database.reminderDao().deleteAllReminders()

        //WHEN - The reminders are deleted
        val result = database.reminderDao().getReminders()
        assertThat(result.size, `is`(0))
    }
}