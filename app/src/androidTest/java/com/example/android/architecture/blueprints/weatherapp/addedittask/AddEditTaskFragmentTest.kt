package com.example.android.architecture.blueprints.weatherapp.addedittask

import android.content.Context
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.weatherapp.R
import com.example.android.architecture.blueprints.weatherapp.data.Result
import com.example.android.architecture.blueprints.weatherapp.data.source.TasksRepository
import com.example.android.architecture.blueprints.weatherapp.launchFragmentInHiltContainer
import com.example.android.architecture.blueprints.weatherapp.util.getTasksBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Integration test for the Add Task screen.
 */
@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@HiltAndroidTest
class AddEditTaskFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: TasksRepository

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }

    @Test
    fun emptyTask_isNotSaved() {
        // GIVEN - On the "Add Task" screen.
        val bundle = AddEditTaskFragmentArgs(
            null,
            getApplicationContext<Context>().getString(R.string.add_task)
        ).toBundle()

        launchFragmentInHiltContainer<AddEditTaskFragment>(bundle, R.style.AppTheme)

        // WHEN - Enter invalid title and description combination and click save
        onView(withId(R.id.add_task_title_edit_text)).perform(clearText())
        onView(withId(R.id.add_task_description_edit_text)).perform(clearText())
        onView(withId(R.id.save_task_fab)).perform(click())

        // THEN - Entered Task is still displayed (a correct task would close it).
        onView(withId(R.id.add_task_title_edit_text)).check(matches(isDisplayed()))
    }

    @Test
    fun validTask_navigatesBack() {
        // GIVEN - On the "Add Task" screen.
        val navController = TestNavHostController(getApplicationContext())
        launchFragment(navController)

        // WHEN - Valid title and description combination and click save
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("title"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // THEN - Verify that we navigated back to the tasks screen.
        assertEquals(navController.currentDestination?.id, R.id.tasks_fragment_dest)
    }

    @Test
    fun validTask_isSaved() {
        // GIVEN - On the "Add Task" screen.
        val navController = TestNavHostController(getApplicationContext())
        launchFragment(navController)

        // WHEN - Valid title and description combination and click save
        onView(withId(R.id.add_task_title_edit_text)).perform(replaceText("title"))
        onView(withId(R.id.add_task_description_edit_text)).perform(replaceText("description"))
        onView(withId(R.id.save_task_fab)).perform(click())

        // THEN - Verify that the repository saved the task
        val tasks = (repository.getTasksBlocking(true) as Result.Success).data
        assertEquals(tasks.size, 1)
        assertEquals(tasks[0].title, "title")
        assertEquals(tasks[0].description, "description")
    }

    private fun launchFragment(navController: TestNavHostController) {
        val bundle = AddEditTaskFragmentArgs(
            null,
            getApplicationContext<Context>().getString(R.string.add_task)
        ).toBundle()
        launchFragmentInHiltContainer<AddEditTaskFragment>(bundle, R.style.AppTheme) {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.add_edit_task_fragment_dest)
            Navigation.setViewNavController(requireView(), navController)
        }
    }
}
