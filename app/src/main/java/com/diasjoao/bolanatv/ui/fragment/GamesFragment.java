package com.diasjoao.bolanatv.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diasjoao.bolanatv.R;
import com.diasjoao.bolanatv.ui.adapter.MainRecyclerAdapter;
import com.diasjoao.bolanatv.model.Game;
import com.shuhart.stickyheader.StickyHeaderItemDecorator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesFragment extends Fragment {
    View view;
    RecyclerView recyclerView;

    Map<String, List<Game>> games;

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
    public static GamesFragment newInstance(Map<String, List<Game>> param1) {
        GamesFragment fragment = new GamesFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_games, container, false);
        games = (HashMap< String, List<Game>>)getArguments().getSerializable(ARG_PARAM1);

        recyclerView = view.findViewById(R.id.sectionRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter(getActivity(), games);
        recyclerView.setAdapter(mainRecyclerAdapter);

        StickyHeaderItemDecorator decorator = new StickyHeaderItemDecorator(mainRecyclerAdapter);
        decorator.attachToRecyclerView(recyclerView);

        return view;
    }
}