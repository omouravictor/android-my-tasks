package com.example.tasks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasks.R;
import com.example.tasks.adapter.FinishedTaskAdapter;

public class FinishedTaskFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    FinishedTaskAdapter adapter;

    public FinishedTaskFragment(FinishedTaskAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_finished_tasks, container, false);
        recyclerView = view.findViewById(R.id.rvFinishedTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return view;
    }

}