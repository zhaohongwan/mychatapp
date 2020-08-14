package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mychatapp.SMS.SMSBroadcastReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PreStartActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_start);

        //initialize firebase auth
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        progressBar = findViewById(R.id.progressBar);

        //if the current mobile device does not have the Firebase ID token
        if (user == null){
            progressBar.setVisibility(View.VISIBLE);
            //create an anonymous account for the current user
            auth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(PreStartActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PreStartActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check if the current device has the Firebase ID token
        if (user != null){
            Intent intent = new Intent(PreStartActivity.this, MainActivity.class);
            Toast.makeText(PreStartActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }
    }

}