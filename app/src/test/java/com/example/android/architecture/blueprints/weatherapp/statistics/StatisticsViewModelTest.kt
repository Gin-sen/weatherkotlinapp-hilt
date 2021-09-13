package com.example.android.architecture.blueprints.weatherapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.weatherapp.FakeFailingTasksRemoteDataSource
import com.example.android.architecture.blueprints.weatherapp.MainCoroutineRule
import com.example.android.architecture.blueprints.weatherapp.data.Task
import com.example.android.architecture.blueprints.weatherapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.weatherapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.weatherapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [StatisticsViewModel]
 */
@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tasksRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupStatisticsViewModel() {
        tasksRepository = FakeRepository()

        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @Test
    fun loadEmptyTasksFromRepository_EmptyResults() = mainCoroutineRule.runBlockingTest {
        // Given an initialized StatisticsViewModel with no tasks

        // Then the results are empty
        assertThat(statisticsViewModel.empty.getOrAwaitValue()).isTrue()
    }

    @Test
    fun loadNonEmptyTasksFromRepository_NonEmptyResults() {
        // We initialise the tasks to 3, with one active and two completed
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        val task4 = Task("Title4", "Description4", true)
        tasksRepository.addTasks(task1, task2, task3, task4)

        // Then the results are not empty
        assertThat(statisticsViewModel.empty.getOrAwaitValue())
            .isFalse()
        assertThat(statisticsViewModel.activeTasksPercent.getOrAwaitValue())
            .isEqualTo(25f)
        assertThat(statisticsViewModel.completedTasksPercent.getOrAwaitValue())
            .isEqualTo(75f)
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_CallErrorToDisplay() {
        val errorViewModel = StatisticsViewModel(
            DefaultTasksRepository(
                FakeFailingTasksRemoteDataSource,
                FakeFailingTasksRemoteDataSource,
                Dispatchers.Main // Main is set in MainCoroutineRule
            )
        )

        // Then an error message is shown
        assertThat(errorViewModel.empty.getOrAwaitValue()).isTrue()
        assertThat(errorViewModel.error.getOrAwaitValue()).isTrue()
    }

    @Test
    fun loadTasks_loading() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // Load the task in the viewmodel
        statisticsViewModel.refresh()

        // Then progress indicator is shown
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue()).isTrue()

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then progress indicator is hidden
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue()).isFalse()
    }
}
