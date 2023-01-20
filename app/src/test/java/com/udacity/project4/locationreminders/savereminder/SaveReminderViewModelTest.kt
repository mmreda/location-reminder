package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import android.provider.Settings.Global.getString
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.DummyReminderData
import com.udacity.project4.locationreminders.DummyReminderData.reminder
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.RemindersListViewModel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.junit.*
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var application: Application

    @Before
    fun setup() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        application = ApplicationProvider.getApplicationContext()
        viewModel = SaveReminderViewModel(application, fakeDataSource)
    }

    @After
    fun clearData() = runBlockingTest{
        fakeDataSource.deleteAllReminders()
    }

    @Test
    fun saveReminder_loading() = runBlockingTest{
        // WHEN load reminders
        mainCoroutineRule.pauseDispatcher()
        viewModel.saveReminder(DummyReminderData.reminder)

        // THEN the progress indicator is shown
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute stop indicator
        mainCoroutineRule.resumeDispatcher()

        // THEN: the progress indicator is hidden
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun saveReminder_success() = runBlockingTest{
        // WHEN Save a reminder
        viewModel.saveReminder(DummyReminderData.reminder)


        // THEN The reminder is saved and navigate back
        assertThat(viewModel.showToast.getOrAwaitValue(),
            `is`(application.getString(R.string.reminder_saved)))
        Assert.assertEquals(viewModel.navigationCommand.getOrAwaitValue(), NavigationCommand.Back)
    }

    @Test
    fun validateEnteredData_titleEmpty() {
        // GIVEN a reminder with empty title
        val reminder = DummyReminderData.reminder.copy()
        reminder.title = ""

        // WHEN validate entered data
        val boolean = viewModel.validateEnteredData(reminder)

        // THEN an error is shown
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_enter_title))
        assertThat(boolean, CoreMatchers.`is`(false))
    }

    @Test
    fun validateEnteredData_locationEmpty() {
        // GIVEN a reminder with empty location
        val reminder = DummyReminderData.reminder.copy()
        reminder.location = ""

        // WHEN validate entered data
        val boolean = viewModel.validateEnteredData(reminder)

        // THEN an error is shown
        assertThat(viewModel.showSnackBarInt.getOrAwaitValue(), `is`(R.string.err_select_location))
        assertThat(boolean, CoreMatchers.`is`(false))
    }

    @Test
    fun validateEnteredData_success() {
        // GIVEN a reminder with valid data
        val reminder = DummyReminderData.reminder

        // WHEN validate entered data
        val boolean = viewModel.validateEnteredData(reminder)

        // THEN no error is shown
        assertThat(boolean, CoreMatchers.`is`(true))
    }
}