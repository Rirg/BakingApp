package com.example.ricardo.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.ricardo.bakingapp.activities.MenuActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Ricardo on 11/17/17.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MenuActivityTest {

    @Rule
    public ActivityTestRule<MenuActivity> mActivityRule = new ActivityTestRule<>(MenuActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        // Get the idling resource
        mIdlingResource = mActivityRule.getActivity().getIdlingResource();

        // Register the idling resource before doing any test
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void clickMenuItem_OpenStepsList() {
        // Check that the view with the specified text is displayed
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));

        // Click on the first item
        onView(withId(R.id.recipes_list_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check that the view with the specified text is displayed
        onView(withText("Starting prep")).check(matches(isDisplayed()));

    }

    @Test
    public void clickMenuItem_clickStepItem_OpenStepDetail() {
        // Click on the first item
        onView(withId(R.id.recipes_list_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Click on the first item
        onView(withId(R.id.steps_list_rv))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.step_description_tv)).check(matches(withText("Recipe Introduction")));


    }


    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            // Unregister the idling resource after the tests are completed
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}