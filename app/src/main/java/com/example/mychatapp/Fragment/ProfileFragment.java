package com.example.mychatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.mychatapp.PhoneAuthenticationActivity;
import com.example.mychatapp.PreStartActivity;
import com.example.mychatapp.R;
import com.example.mychatapp.viewmodel.LocalUserViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    private Button logout_button;
    private TextView username;
    private LocalUserViewModel localUserViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logout_button = view.findViewById(R.id.logout_button);
        username = view.findViewById(R.id.username);

        //display the current mobile device's phone number
        final String phonenumber = getArguments().getString("phonenumber");
        username.setText(phonenumber);

        localUserViewModel = ViewModelProviders.of(getActivity()).get(LocalUserViewModel.class);

        //set the logout function for the logout card view
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
                localUserViewModel.updateUserStatus(phonenumber, "None");
            }
        });

        return view;
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getContext(), PreStartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

}
