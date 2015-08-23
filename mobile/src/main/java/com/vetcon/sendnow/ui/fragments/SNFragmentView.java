package com.vetcon.sendnow.ui.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.vetcon.sendnow.R;
import java.lang.ref.WeakReference;

/** -----------------------------------------------------------------------------------------------
 *  [SNFragmentView] CLASS
 *  PROGRAMMER: Michael Yoon Huh (Huh X0015)
 *  DESCRIPTION: SNFragmentView class contains methods for adding and removing Fragment-related
 *  views, as well as animating fragment transitions.
 *  -----------------------------------------------------------------------------------------------
 */

public class SNFragmentView {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // LOGGING VARIABLES
    private static final String LOG_TAG = SNFragmentView.class.getSimpleName();

    /** FRAGMENT VIEW METHODS __________________________________________________________________ **/

    // addFragment(): Sets up the fragment view.
    public static void addFragment(Fragment fragment, ViewGroup container, int containerId,
                                   final String fragType, Boolean isAnimated,
                                   WeakReference<AppCompatActivity> refActivity) {

        if ((refActivity.get() != null) && (!refActivity.get().isFinishing())) {

            // Initializes the manager and transaction objects for the fragments.
            FragmentManager fragMan = refActivity.get().getSupportFragmentManager();
            FragmentTransaction fragTrans = fragMan.beginTransaction();
            fragTrans.replace(containerId, fragment, fragType);
            fragTrans.addToBackStack(fragType); // Adds fragment to the fragment stack.

            // Makes the changes to the fragment manager and transaction objects.
            fragTrans.commitAllowingStateLoss();

            // Sets up the transition animation.
            if (isAnimated) {
                setFragmentTransition(fragType, container, true, refActivity);
            }

            // Displays the fragment view without any transition animations.
            else {
                container.setVisibility(View.VISIBLE); // Displays the fragment.
            }
        }
    }

    // removeFragment(): This method is responsible for removing the fragment view.
    public static void removeFragment(ViewGroup container, final String fragType, Boolean isAnimated,
                                      WeakReference<AppCompatActivity> refActivity) {

        if ((refActivity.get() != null) && (!refActivity.get().isFinishing())) {

            // Animates the fragment transition.
            if (isAnimated) {
                setFragmentTransition(fragType, container, false, refActivity);
            }

            // The fragment is removed from the view layout.
            else {

                // Initializes the manager and transaction objects for the fragments.
                FragmentManager fragMan = refActivity.get().getSupportFragmentManager();
                Fragment currentFragment = refActivity.get().getSupportFragmentManager().findFragmentByTag(fragType);
                fragMan.beginTransaction().remove(currentFragment).commitAllowingStateLoss();
                fragMan.popBackStack(); // Pops the fragment from the stack.
                container.removeAllViews(); // Removes all views in the layout.
                container.setVisibility(View.INVISIBLE); // Hides the fragment.

                Log.d(LOG_TAG, "removeFragment(): Fragment has been removed.");
            }
        }
    }

    // setFragmentTransition(): Sets the fragment transition animation, based on the specified
    // fragment type.
    public static void setFragmentTransition(final String fragType,
                                             final ViewGroup container,
                                             final Boolean isAppearing,
                                             final WeakReference<AppCompatActivity> refActivity) {

        int animationResource; // References the animation XML resource file.

        // Sets the animation XML resource file, based on the fragment type.
        if (fragType.equals("WALLET")) {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.slide_down; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.slide_up; // Sets the animation XML resource file.
            }
        }

        else if (fragType.equals("PLACES")) {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.slide_right; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.slide_left; // Sets the animation XML resource file.
            }
        }

        else {

            // FRAGMENT APPEARANCE ANIMATION:
            if (isAppearing) {
                animationResource = R.anim.slide_up; // Sets the animation XML resource file.
            }

            // FRAGMENT REMOVAL ANIMATION:
            else {
                animationResource = R.anim.slide_down; // Sets the animation XML resource file.
            }
        }

        // Loads the animation from the XML animation resource file.
        Animation fragmentAnimation = AnimationUtils.loadAnimation(refActivity.get().getBaseContext(), animationResource);

        // Sets the AnimationListener for the animation.
        fragmentAnimation.setAnimationListener(new Animation.AnimationListener() {

            // onAnimationStart(): Runs when the animation is started.
            @Override
            public void onAnimationStart(Animation animation) {

                // FRAGMENT APPEARANCE ANIMATION:
                if (isAppearing) {
                    container.setVisibility(View.VISIBLE); // Displays the fragment.
                }
            }

            // onAnimationEnd(): The fragment is removed after the animation ends.
            @Override
            public void onAnimationEnd(Animation animation) {

                Log.d(LOG_TAG, "setFragmentTransition(): Fragment animation has ended.");

                // FRAGMENT REMOVAL ANIMATION:
                if (!isAppearing) {

                    // Removes the fragment from the view.
                    removeFragment(container, fragType, false, refActivity);
                }
            }

            // onAnimationRepeat(): Runs when the animation is repeated.
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        container.startAnimation(fragmentAnimation); // Starts the animation.
    }
}