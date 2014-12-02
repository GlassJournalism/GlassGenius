package io.glassjournalism.glassgenius.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;

import java.io.File;

import io.filepicker.FilePickerAPI;
import io.glassjournalism.glassgenius.FilePickerImageResponse;
import io.glassjournalism.glassgenius.R;
import io.glassjournalism.glassgenius.fragments.CardHistoryFragment;
import io.glassjournalism.glassgenius.fragments.GlassCardCreationFragment;
import io.glassjournalism.glassgenius.fragments.NavigationDrawerFragment;
import io.glassjournalism.glassgenius.fragments.PhotoListFragment;
import io.glassjournalism.glassgenius.fragments.StoryFragment;
import io.glassjournalism.glassgenius.fragments.StoryListFragment;
import io.glassjournalism.glassgenius.fragments.VideoListFragment;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, StoryListFragment.OnFragmentInteractionListener, StoryFragment.OnFragmentInteractionListener, PhotoListFragment.OnFragmentInteractionListener, VideoListFragment.OnFragmentInteractionListener, GlassCardCreationFragment.OnFragmentInteractionListener, CardHistoryFragment.OnFragmentInteractionListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    Toolbar toolbar;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);

//        getActionBar().setHomeAsUpIndicator(drawerArrowDrawable);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                drawerLayout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {};

        drawerLayout.setDrawerListener(drawerToggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        FilePickerAPI.setKey(FILEPICKER_API_KEY);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        Fragment fragment;

        switch (position) {
            case 0:
                fragment = CardHistoryFragment.newInstance("", "");
                break;
            case 1:
                fragment = StoryListFragment.newInstance("", "");
                break;
            case 2:
                fragment = PhotoListFragment.newInstance("", "");
                break;
            case 3:
                fragment = VideoListFragment.newInstance("", "");
                break;
            default:
                fragment = CardHistoryFragment.newInstance("", "");
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section0);
                break;
            case 2:
                mTitle = getString(R.string.title_section1);
                break;
            case 3:
                mTitle = getString(R.string.title_section2);
                break;
            case 4:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    public void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null && data.getData() != null) {
            Log.i("GLASSGENIUS", "Intent response captured!");

            Uri selectedImage = data.getData();

            Log.i("GlassGenius", selectedImage.toString());

            File file = new File(selectedImage.toString());

            ContentResolver contentResolver = this.getContentResolver();
            String mimeType = contentResolver.getType(Uri.fromFile(file));

            TypedFile typedFile = new TypedFile(mimeType, file);

            RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("https://www.filepicker.io").build();

            FilePickerService service = restAdapter.create(FilePickerService.class);

            service.postPhoto(typedFile, new Callback<FilePickerImageResponse>() {

                @Override
                public void success(FilePickerImageResponse filePickerImageResponse, Response response) {
                    Log.i("GlassGenius", filePickerImageResponse.getUrl());
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("GlassGenius", "Upload failed!");
                }
            });
        }
    }

    public interface FilePickerService {
        void postPhoto(@Part("fileUpload") TypedFile uri, Callback<FilePickerImageResponse> cb);
    }
}
