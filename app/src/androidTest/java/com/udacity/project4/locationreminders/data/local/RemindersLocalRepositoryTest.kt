package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localRepository: RemindersLocalRepository
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localRepository =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun insertReminder_and_getReminderById_success() = runBlockingTest {
        //GIVEN - insert a reminder
        val reminder = ReminderDTO("Reminder to visit pyramids", "",
            "Pyramids of Giza", 29.991763586846275, 31.133968294733766)
        localRepository.saveReminder(reminder)

        //WHEN - get a reminder from the local repository by id
        val result = localRepository.getReminder(reminder.id)

        //THEN - The loaded data contains the same values
        assertThat(result, CoreMatchers.not(CoreMatchers.nullValue()))
        result as Result.Success
        assertThat(result.data.id, `is`(reminder.id))
        assertThat(result.data.title, `is`(reminder.title))
        assertThat(result.data.location, `is`(reminder.location))
        assertThat(result.data.latitude, `is`(reminder.latitude))
        assertThat(result.data.longitude, `is`(reminder.longitude))
    }

    @Test
    fun getReminderById_error() = runBlockingTest {
        //WHEN - the reminder is retrieved
        val result = localRepository.getReminder("1")

        //THEN - The result data is not found
        result as Result.Error
        assertThat(result.message, Matchers.`is`("Reminder not found!"))
    }

    @Test
    fun deleteAllReminders() = runBlockingTest {
        //GIVEN - insert a reminders
        val reminder1 = ReminderDTO("Reminder1", "Desc1", "Location1", 29.991763586846275, 31.133968294733766)
        val reminder2 = ReminderDTO("Reminder2", "Desc2", "Location2", 29.976449889966347, 31.01473648790832)
        localRepository.saveReminder(reminder1)
        localRepository.saveReminder(reminder2)

        //WHEN - delete all reminders
        localRepository.deleteAllReminders()

        //THEN - The reminders are deleted
        val result = localRepository.getReminders() as Result.Success
        assertThat(result.data.size, `is`(0))
    }
}