package com.example.android.technews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    /** Tag for log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /** Creates a private constructor for QueryUtils. */
    private QueryUtils() {
    }

    /** Query the Guardian API and return a {@Link Event} object to represent a single news story.*/
    public static List<Event> fetchNewsInfo(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Event> earthquakes = extractNews(jsonResponse);

        // Return the {@link Event}
        return earthquakes;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        // Set up the Read and Connect Timeout values for network connection activities
        int READ_TIMEOUT = 10000; // milliseconds
        int CONNECT_TIMEOUT = 15000; // milliseconds

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Event} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Event> extractNews(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<Event> events = new ArrayList<>();

        // This section attempts to parse the JSON response. If there's a problem with the way
        // the JSON is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject root = new JSONObject(newsJSON);

            // Extract the "response" JSON object
            JSONObject response = root.getJSONObject("response");

            // Extract the "results" JSON Array
            JSONArray resultsArray = response.getJSONArray("results");

            // For each event in the Array, create an {@link Event} object
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single event at position i within the list of events
                JSONObject currentEvent = resultsArray.getJSONObject(i);

                // Extract the Section Name
                String section = currentEvent.getString("sectionName");

                // Extract the Article Title
                String title = currentEvent.getString("webTitle");

                // Extract the author if available
                String author;
                JSONArray tags = currentEvent.getJSONArray("tags");

                if (tags.length() > 0) {
                    JSONObject tagObject = tags.getJSONObject(0);
                    author = tagObject.getString("webTitle");
                } else {
                    author = "Unavailable";
                }

                // Extract the Publish Date and Time
                String date = currentEvent.getString("webPublicationDate");

                // Extract the value for the key called "url"
                String url = currentEvent.getString("webUrl");

                // Create a new {@link Event} object with the info from the JSON response
                // and add the new {@link Event} to the list of events.
                events.add(new Event(section, title, author, date, url));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return events;
    }
}