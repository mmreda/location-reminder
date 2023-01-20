package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.DummyReminderData
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: RemindersListViewModel

    @Before
    fun setup() {
        stopKoin()
        fakeDataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun clearData() = runBlockingTest{
        fakeDataSource.deleteAllReminders()
    }

    @Test
    fun loadReminders_loading() = runBlockingTest{
        // WHEN load reminders
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()

        // THEN the progress indicator is shown
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(true))

        // Execute stop indicator
        mainCoroutineRule.resumeDispatcher()

        // THEN: the progress indicator is hidden
        assertThat(viewModel.showLoading.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_success() = runBlockingTest{
        // GIVEN insert a reminders
        DummyReminderData.reminders.forEach {
            fakeDataSource.saveReminder(it)
        }

        // WHEN Load reminders
        viewModel.loadReminders()


        // THEN The loaded data contains the expected values
        val result = viewModel.remindersList.getOrAwaitValue()
        assertThat(result.size, `is`(DummyReminderData.reminders.size))
        result.indices.forEach {
            assertThat(result[it].title, `is`(DummyReminderData.reminders[it].title))
        }
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(false))
    }

    @Test
    fun loadReminders_failure() = runBlockingTest{
        // GIVEN Return an error
        fakeDataSource.setShouldReturnError(true)

        // WHEN Load reminders
        viewModel.loadReminders()

        // THEN The error message is shown
        assertThat(viewModel.showSnackBar.getOrAwaitValue(), `is`("Reminders not found!"))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
    }

    @Test
    fun loadReminders_empty() = runBlockingTest{
        // GIVEN Empty list of Reminders
        fakeDataSource.deleteAllReminders()

        // WHEN Load reminders
        viewModel.loadReminders()

        // THEN Size of Reminders is zero
        val result = viewModel.remindersList.getOrAwaitValue()
        assertThat(result.size, `is`(0))
        assertThat(viewModel.showNoData.getOrAwaitValue(), `is`(true))
    }
}