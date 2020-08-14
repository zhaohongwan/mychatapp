package com.example.mychatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mychatapp.Adapter.User_Adapter;
import com.example.mychatapp.AddNewContactActivity;
import com.example.mychatapp.MainActivity;
import com.example.mychatapp.MessageActivity;
import com.example.mychatapp.Model.ChattedUser;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.R;
import com.example.mychatapp.SMS.SMSBroadcastReceiver;
import com.example.mychatapp.viewmodel.ChattedUserViewModel;
import com.example.mychatapp.viewmodel.LocalchatViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UserFragment extends Fragment {
    private RecyclerView recyclerView;
    private User_Adapter user_adapter;
    private FloatingActionButton addNewContact;
    private List<ChattedUser> mUsers;

    ChattedUserViewModel chattedUserViewModel;
    LocalchatViewModel localchatViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        addNewContact = view.findViewById(R.id.addNewContact);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        chattedUserViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ChattedUserViewModel.class);
        localchatViewModel = ViewModelProviders.of(getActivity()).get(LocalchatViewModel.class);

        //go to the AddNewContactActivity, so the user can text with others by enter their phone number.
        addNewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddNewContactActivity.class);
                startActivity(intent);
            }
        });

        //get the current user's user type(Individual or Official)
        assert getArguments() != null;

        //display all of the chatted_user that stored in the local database
        chattedUserViewModel.loadAllUsers().observe(getActivity(), new Observer<List<ChattedUser>>() {
            @Override
            public void onChanged(List<ChattedUser> chattedUsers) {
                mUsers.addAll(chattedUsers);
                user_adapter = new User_Adapter(getContext(), mUsers);
                recyclerView.setAdapter(user_adapter);
            }
        });

        return view;
    }
}
