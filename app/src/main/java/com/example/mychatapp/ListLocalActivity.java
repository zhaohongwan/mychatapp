package com.example.mychatapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapp.Adapter.ListAdapter;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.viewmodel.LocalchatViewModel;

import java.util.List;

public class ListLocalActivity extends AppCompatActivity {

    ListAdapter listAdapter;
    LocalchatViewModel localchatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_local);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        listAdapter = new ListAdapter();
        recyclerView.setAdapter(listAdapter);


        localchatViewModel = ViewModelProviders.of(this).get(LocalchatViewModel.class);
        localchatViewModel.loadAllChats().observe(this, new Observer<List<Localchat>>() {
            @Override
            public void onChanged(List<Localchat> localchats) {
                listAdapter.setChats(localchats);
            }
        });
    }
}