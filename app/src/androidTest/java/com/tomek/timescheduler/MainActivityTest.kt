package com.tomek.timescheduler

import android.app.Activity
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Rule
import org.junit.Test

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
        TimeScheduler.setTimeSchedulerHandler(BiFunction { default, tag ->
            if (tag == FAB_DELAY) Schedulers.trampoline() else default
        })

        rule.launchActivity(null)
        Espresso.onView(ViewMatchers.withId(R.id.fab))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(MESSAGE))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
