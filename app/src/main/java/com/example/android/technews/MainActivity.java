package com.example.android.technews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Event>> {

    // Create global private variable to monitor network connectivity.
    private boolean mIsConnected;
    // Create global private variable to reference the active adapter.
    private NewsAdapter mAdapter;

    // Private variable that tags the Empty TextView on the main screen.
    private TextView mEmptyView;

    /** Tag for the log messages */
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**URL to query Guardian for Tech news */
    private static final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search";

    /** Constant value for the loader ID. We can choose any integer. */
    public static final int NEWS_LOADER_ID = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Determine if there is a Network Connection
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        mIsConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = findViewById(R.id.list);

        // Sets an empty ListView if there is no news info
        mEmptyView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<Event>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected event.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current news event that was clicked on
                Event currentEvent = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass to Internet constructor)
                Uri newsUri = Uri.parse(currentEvent.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        // Get a reference to the LoaderManager to be able to interact with the loaders.
        LoaderManager loaderManager = getLoaderManager();

        // Initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(NEWS_LOADER_ID, null, this);
    }

    @Override
    public Loader<List<Event>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a String value from the preferences and the second parameter is the default
        String fromDate = sharedPrefs.getString(getString(R.string.settings_from_date_key),
                getString(R.string.settings_from_date_default));
        String sortBy = sharedPrefs.getString(getString(R.string.settings_sort_by_key),
                getString(R.string.settings_sort_by_default));

        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // https://content.guardianapis.com/search?api-key=65139045-f62e-4ff8-8fb6-b47512f8d548&
        // q=green%20AND%20technology%20AND%20energy%20AND%20clean%20OR%20renewable&
        // format=json&section=technology&tags=green%20AND%20energy%20AND%20clean%20OR%20renewable&
        // from-date=2017-07-18&show-tags=contributor&order-by=relevance

        // Append query parameter and its value.
        uriBuilder.appendQueryParameter("format","json");
        uriBuilder.appendQueryParameter("api-key","65139045-f62e-4ff8-8fb6-b47512f8d548");
        uriBuilder.appendQueryParameter("q", "technology AND OR energy AND OR clean AND OR " +
                "renewable AND OR green");
        uriBuilder.appendQueryParameter("section", "technology");
        uriBuilder.appendQueryParameter("tags", "technology AND OR energy AND OR clean AND OR " +
                "renewable AND OR green");
        uriBuilder.appendQueryParameter("from-date", fromDate);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", sortBy);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Event>> loader, List<Event> events) {
        // Sets up a progress bar view
        ProgressBar mProgressBar;

        // Removes the progress bar when loading is complete
        mProgressBar = findViewById(R.id.progress_bar);
        mProgressBar.setVisibility(View.GONE);

        // Clear the adapter of previous data
        mAdapter.clear();

        // Set the text in the Empty text view
        if (mIsConnected) {
            mEmptyView.setText(R.string.empty_list);
        } else {
            mEmptyView.setText(R.string.no_connection);
        }

        // If there is a valid list of {@link Event}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (events != null && !events.isEmpty()) {
            mAdapter.addAll(events);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Event>> loader) {
        // Clear the adapter of previous data
        mAdapter.clear();
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
