package com.example.d3.cardstack;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class LeaderboardActivityFragment extends Fragment {

    private ListView resultListView;

    public LeaderboardActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.leaderboards_list, container, false);
        resultListView = (ListView) rootView.findViewById(R.id.searchResults);
        return rootView;
    }
}
