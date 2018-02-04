package com.tomek.timescheduler

import android.app.Activity
import android.support.test.espresso.Espresso
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.view.View
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class MainActivityTest {

    private inline fun <reified T : Activity> create() =
        ActivityTestRule(T::class.java, false, false)

    @Rule
    @JvmField
    val rule: ActivityTestRule<MainActivity> = create()

    @After
    fun cleanup() {
        TimeScheduler.reset()
    }

    @Test
    fun fab_shows_after_some_time() {
        TimeScheduler.timeSchedulerHandler = { default, tag ->
            if (tag == FAB_DELAY_TAG) InstantScheduler else default
        }

        rule.launchActivity(null)
        Espresso.onView(withId(R.id.fab))
            .perform(click())

        Espresso.onView(withText(MESSAGE))
            .check(matches(isDisplayed()))
    }

    @Test
    fun does_not_show_withing_four_seconds() {
        val scheduler = TestScheduler()

        TimeScheduler.timeSchedulerHandler = { default, tag ->
            if (tag == FAB_DELAY_TAG) scheduler else default
        }

        rule.launchActivity(null)
        Espresso.onView(withId(R.id.fab))
            .perform(click())

        scheduler.advanceTimeBy(FAB_DELAY - 1, TimeUnit.SECONDS)

        Espresso.onView(withText(MESSAGE))
            .check(doesNotExist())
    }

    @Test
    fun shows_after_five_seconds() {
        val scheduler = TestScheduler()

        TimeScheduler.timeSchedulerHandler = { default, tag ->
            if (tag == FAB_DELAY_TAG) scheduler else default
        }

        rule.launchActivity(null)

        Espresso.onView(withId(R.id.fab))
            .perform(click())

        scheduler.advanceTimeTo(FAB_DELAY, TimeUnit.SECONDS)

        Espresso.onView(withId(R.id.snackbar_text))
            .check(Exists)
    }

    object Exists : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) =
            assertThat(
                "View is not present in the hierarchy",
                view != null,
                `is`<Boolean>(true)
                      )
    }
}
