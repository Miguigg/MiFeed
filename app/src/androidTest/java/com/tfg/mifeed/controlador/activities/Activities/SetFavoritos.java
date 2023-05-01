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
public class SetFavoritos {

  @Rule
  public ActivityScenarioRule<BienvenidaActivity> mActivityScenarioRule =
      new ActivityScenarioRule<>(BienvenidaActivity.class);

  @Test
  public void setFavoritos() {


    ViewInteraction constraintLayout3 =
        onView(
            allOf(
                withId(R.id.btnModificar),
                childAtPosition(childAtPosition(withId(R.id.framePrensa), 0), 4),
                isDisplayed()));
    constraintLayout3.perform(click());

    ViewInteraction switch_ =
        onView(
            allOf(
                withId(R.id.switchTecnologia),
                childAtPosition(childAtPosition(withId(android.R.id.content), 0), 17),
                isDisplayed()));
    switch_.perform(click());

    ViewInteraction switch_2 =
        onView(
            allOf(
                withId(R.id.switchSalud),
                childAtPosition(childAtPosition(withId(android.R.id.content), 0), 15),
                isDisplayed()));
    switch_2.perform(click());

    ViewInteraction constraintLayout4 =
        onView(
            allOf(
                withId(R.id.btnAceptar),
                withContentDescription("Deporte"),
                childAtPosition(childAtPosition(withId(android.R.id.content), 0), 19),
                isDisplayed()));
    constraintLayout4.perform(click());
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
