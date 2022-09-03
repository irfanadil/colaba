package com.rnsoft.colabademo

import android.support.test.rule.ActivityTestRule
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginFragmentTest {

        @Rule
        var mMainActivityRule:ActivityTestRule<SignUpFlowActivity> = ActivityTestRule(SignUpFlowActivity::class.java)

        @Test
        fun testEvent() {
            val scenario = launchActivity<SignUpFlowActivity>()
            //Espresso.onView(ViewMatchers.withId(R.id.)).perform(ViewActions.click())
        }



        @Test
        fun editTextTest() {
            //onView(withId(R.id.editText_main)).check(matches(withText(containsString("SOME_TEXT"))))
        }

}