package com.vetcon.sendnow.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.simplify.android.sdk.Simplify;
import com.vetcon.sendnow.R;
import com.vetcon.sendnow.ui.fragments.SNFragmentView;
import com.vetcon.sendnow.ui.layout.SNUnbind;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SNMainActivity extends AppCompatActivity  {

    /** CLASS VARIABLES ________________________________________________________________________ **/

    // ACTIVITY VARIABLES
    private static WeakReference<AppCompatActivity> weakRefActivity = null; // Used to maintain a weak reference to the activity.

    // FRAGMENT VARIABLES
    private String currentFragment = ""; // Used to determine which fragment is currently active.

    // LAYOUT VARIABLES
    private ActionBarDrawerToggle drawerToggle; // References the toolbar drawer toggle button.

    // LOGGING VARIABLES
    private static final String LOG_TAG = SNMainActivity.class.getSimpleName();

    // VIEW INJECTION VARIABLES
    @Bind(R.id.sn_fragment_container) FrameLayout fragmentContainer;
    @Bind(R.id.sn_action_button) FloatingActionButton actionButton;
    @Bind(R.id.sn_toolbar) Toolbar sn_main_toolbar;

    /** ACTIVITY METHODS _______________________________________________________________________ **/

    // onCreate(): The initial function that is called when the activity is run. onCreate() only runs
    // when the activity is first started.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        weakRefActivity = new WeakReference<AppCompatActivity>(this); // Creates a weak reference of this activity.
        setupLayout(); // Sets up the layout for the activity.
    }

    // onDestroy(): This function runs when the activity has terminated and is being destroyed.
    // Calls recycleMemory() to free up memory allocation.
    @Override
    public void onDestroy() {
        recycleMemory(); // Recycles all View objects to free up memory resources.
        super.onDestroy();
    }

    /** ACTIVITY EXTENSION METHODS _____________________________________________________________ **/

    // onConfigurationChanged(): If the screen orientation changes, this function loads the proper
    // layout, as well as updating all layout-related objects.
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);

        setupLayout(); // Updates the layout for the activity.

        // Drawer toggle status is updated when the screen orientation changes.
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sn_main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // to get back MaskedWallet using call back method.
        if (Simplify.handleAndroidPayResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // onPostCreate(): Synchronizes the toggle state after onRestoreInstanceState() has occurred.
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    /** PHYSICAL BUTTON FUNCTIONALITY __________________________________________________________ **/

    // BACK KEY:
    // onBackPressed(): Defines the action to take when the physical back button key is pressed.
    @Override
    public void onBackPressed() {
        finish();  // Finishes the activity.
    }

    /** LAYOUT METHODS _________________________________________________________________________ **/

    private void setupLayout() {

        setContentView(R.layout.sn_main_activity_layout);
        ButterKnife.bind(this); // ButterKnife view injection initialization.

        setUpToolbar(); // Sets up the toolbar for the activity.
    }

    /** FRAGMENT METHODS _______________________________________________________________________ **/

    // changeFragment(): Changes the fragment views.
    private void changeFragment(Fragment frag, String fragToAdd, String fragToRemove, String subtitle,
                                Boolean isAnimated) {

        Log.d(LOG_TAG, "changeFragment(): Fragment changed.");

        // Removes the SSArtistsFragment from the stack.
        SNFragmentView.removeFragment(fragmentContainer, fragToRemove, false, weakRefActivity);

        // Adds the fragment to the primary fragment container.
        SNFragmentView.addFragment(frag, fragmentContainer, R.id.sn_fragment_container, fragToAdd, isAnimated, weakRefActivity);

        // Sets the name of the action bar.
        //SNActionBar.setupActionBar(fragToAdd, subtitle, this);
        currentFragment = fragToAdd; // Sets the current active fragment.
    }

    // displayFragmentDialog(): Displays the DialogFragment view for the specified fragment.
    private void displayFragmentDialog(DialogFragment frag, String fragType) {
        FragmentManager fragMan = getSupportFragmentManager(); // Sets up the FragmentManager.
        frag.show(fragMan, fragType); // Displays the DialogFragment.
    }

    /** TOOLBAR FUNCTIONALITY __________________________________________________________________ **/

    // setUpToolbar(): Sets up the Material Design style toolbar for the activity.
    private void setUpToolbar() {

        // Sets the references for the Drawer-related objects.
        String[] snDrawerSettings = getResources().getStringArray(R.array.drawer_list);
        DrawerLayout snDrawerLayout = (DrawerLayout) findViewById(R.id.sn_drawer_layout);
        ListView snDrawerList = (ListView) findViewById(R.id.sn_main_left_drawer_list);

        // Initializes the Material Design style Toolbar object for the activity.
        if (sn_main_toolbar != null) {
            sn_main_toolbar.setTitle(R.string.app_name);
            setSupportActionBar(sn_main_toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Sets the Drawer toggle button for the Toolbar object.
        drawerToggle = new ActionBarDrawerToggle(this, snDrawerLayout, sn_main_toolbar, R.string.drawer_open, R.string.drawer_close) {

            // onDrawerClosed(): Called when a drawer has settled in a completely closed state.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // Creates a call to onPrepareOptionsMenu() method.
            }

            // onDrawerOpened(): Called when a drawer has settled in a completely open state.
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // Creates a call to onPrepareOptionsMenu() method.
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true); // Draws the toggle button indicator.
        snDrawerLayout.setDrawerListener(drawerToggle); // Sets the listener for the toggle button.

        // Sets the adapter for the drawer list view.
        snDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.sn_drawer_list_layout, snDrawerSettings));

        // Retrieves the DrawerLayout to set the status bar color. This only takes effect on Lollipop,
        // or when using translucentStatusBar on KitKat.
        snDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.sn_toolbar_dark_color));
    }

    /** RECYCLE METHODS ________________________________________________________________________ **/

    // recycleMemory(): Recycles all View objects to clear up memory resources prior to Activity
    // destruction.
    private void recycleMemory() {

        // Unbinds all Drawable objects attached to the current layout.
        try { SNUnbind.unbindDrawables(findViewById(R.id.sn_main_activity_layout)); }
        catch (NullPointerException e) { e.printStackTrace(); } // Prints error message.
    }
}
