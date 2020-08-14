package com.example.mychatapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mychatapp.R;
import com.example.mychatapp.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OptionFragment extends Fragment {

    private Button Upgrade_button;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option, container, false);

        Upgrade_button = view.findViewById(R.id.Upgrade_button);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //using to upgrade the anonymous account to a registered account
        Upgrade_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StartActivity.class);
                //delete the current anonymous account from firebase
                firebaseUser.delete();
                startActivity(intent);
            }
        });

        return view;
    }

}
