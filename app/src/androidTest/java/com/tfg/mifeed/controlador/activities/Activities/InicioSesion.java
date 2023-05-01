package com.tfg.mifeed.controlador.activities.Activities;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.tfg.mifeed.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class InicioSesion {

  @Rule
  public ActivityScenarioRule<BienvenidaActivity> mActivityScenarioRule =
      new ActivityScenarioRule<>(BienvenidaActivity.class);

  @Test
  public void inicioSesion() {
    ViewInteraction constraintLayout =
        onView(
            allOf(
                withId(R.id.toLogin),
                childAtPosition(childAtPosition(withId(android.R.id.content), 0), 4),
                isDisplayed()));
    constraintLayout.perform(click());

    ViewInteraction appCompatEditText =
        onView(
            allOf(
                withId(R.id.editTextCorreo),
                childAtPosition(childAtPosition(withId(R.id.scrollView2), 0), 1)));
    appCompatEditText.perform(
        scrollTo(), replaceText("miguigg1412@gmail.com"), closeSoftKeyboard());

    ViewInteraction appCompatEditText2 =
        onView(
            allOf(
                withId(R.id.editTextTextPass),
                childAtPosition(childAtPosition(withId(R.id.scrollView2), 0), 3)));
    appCompatEditText2.perform(scrollTo(), replaceText("123MGG"), closeSoftKeyboard());

    ViewInteraction constraintLayout2 =
        onView(
            allOf(
                withId(R.id.ejecutarInicio),
                childAtPosition(
                    childAtPosition(withClassName(is("android.widget.LinearLayout")), 5), 0)));
    constraintLayout2.perform(scrollTo(), click());
  }

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher, final int position) {

    return new TypeSafeMatcher<View>() {
      @Override
      public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override
      public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup
            && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }
}
