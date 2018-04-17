package com.expenditrack.expenditrack;

import android.content.Context;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase;
import android.test.ActivityTestCase;
import android.widget.EditText;
import android.widget.Spinner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UnitTestingExpenditrack extends ActivityTestCase {
    @Rule
    public ActivityTestRule<LoginActivity>  mLoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void failedLogin() throws Exception{
        onView(withId(R.id.loginButton))
                .perform(click());
        onView(withId(R.id.username_input))
                .check(matches(isDisplayed()));
    }

    @Test
    public void successfulLogin() throws Exception{
        onView(withId(R.id.username_input))
                .perform(typeText("Mulvaney08"));
        onView(withId(R.id.passwd_input))
                .perform(typeText("123"));
        onView(withId(R.id.loginButton))
                .perform(click());
        assertNotNull(LoginActivity.username);
    }

    @Rule
    public ActivityTestRule<ConfirmReceipt> mConfirmReceipt =
            new ActivityTestRule<>(ConfirmReceipt.class);

    @Test
    public void failedAddReceipt() throws Exception{
        onView(withId(R.id.supplier_name_field_confirm))
                .perform(typeText("Halfords"));
        onView(withId(R.id.total_spent_field_confirm))
                .perform(typeText("hello"));
        onView(withId(R.id.username_input))
                .perform(typeText("Mulvaney08"));
        onView(withId(R.id.confirmReceipt))
                .perform(click());
    }

        @Test
    public void successfulAddReceipt() throws Exception{
        onView(withId(R.id.supplier_name_field_confirm))
                .perform(typeText("Halfords"));
        onView(withId(R.id.total_spent_field_confirm))
                .perform(typeText("50"));
        onView(withId(R.id.confirmReceipt))
                .perform(click());
    }

    @Rule
    public ActivityTestRule<RegisterActivity> mRegister =
            new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void failedRegister() throws Exception{
        onView(withId(R.id.registerButton)).perform(click());
    }

}
