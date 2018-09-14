package com.example.android.technews;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<Event> {

    private static final String LOG_TAG = NewsAdapter.class.getSimpleName();

    // Custom constructor
    public NewsAdapter(Activity context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View listItemView, ViewGroup parent) {
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Event currentEvent = getItem(position);

        // Finds and sets the text in each of the list item text views
        TextView titleView = listItemView.findViewById(R.id.title_text_view);
        titleView.setText(currentEvent.getNewsTitle());

        TextView sectionView = listItemView.findViewById(R.id.section_view);
        sectionView.setText(currentEvent.getSection());

        TextView dateView = listItemView.findViewById(R.id.date_view);
        dateView.setText(currentEvent.getPublishDateTime());

        TextView authorView = listItemView.findViewById(R.id.author_view);
        if (currentEvent.getAuthor().equals("")) {
            authorView.setVisibility(View.GONE);
        } else {
            authorView.setText(currentEvent.getAuthor());
        }

        return listItemView;
    }
}
