package com.diasjoao.bolanatv.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.adapters.CustomAdapter;
import com.diasjoao.bolanatv.models.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesFragment extends Fragment {
    View view;
    RecyclerView recyclerView;

    List<Game> games;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment GamesFragment.
     */
    public static GamesFragment newInstance(List<Game> param1) {
        Log.i("New Instance", String.valueOf(param1.size()));
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_games, container, false);
        games = (ArrayList<Game>)getArguments().getSerializable(ARG_PARAM1);
        Log.i("On Create Viewe", String.valueOf(games.size()));

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        CustomAdapter customAdapter = new CustomAdapter(games);
        recyclerView.setAdapter(customAdapter);

        return view;
    }
}