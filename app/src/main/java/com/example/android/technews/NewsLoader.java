package com.example.android.technews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.EventLog;
import android.util.Log;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<Event>> {

    public static final String LOG_TAG = NewsLoader.class.getSimpleName();

    private String mUrl;

    // Constructor for NewsLoader
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Event> loadInBackground() {
        if (mUrl == null) {
            Log.e(LOG_TAG, "URL not present or is null");
            return null;
        }
        List<Event> eventInfo = QueryUtils.fetchNewsInfo(mUrl);
        return eventInfo;
    }
}
