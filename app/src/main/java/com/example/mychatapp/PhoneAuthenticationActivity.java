package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mychatapp.Model.LocalUser;
import com.example.mychatapp.Model.User;
import com.example.mychatapp.Model.User_Official;
import com.example.mychatapp.generator.DigitalSignature;
import com.example.mychatapp.viewmodel.LocalUserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity {

    private String verificationId;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private EditText verificationCode;
    private AppCompatButton button_submit;

    private String phonenumber;
    private String user_type;

    LocalUserViewModel localUserViewModel;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);

        firebaseAuth = FirebaseAuth.getInstance();
        verificationCode = findViewById(R.id.verificationCode);
        button_submit = findViewById(R.id.button_submit);

        //get the complete phone number from the StartActivity
        phonenumber = getIntent().getStringExtra("result");
        sendCodeToPhone(phonenumber);

        //get the user type that the current choose in the StartActivity
        user_type = getIntent().getStringExtra("usertype");

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otp = verificationCode.getText().toString().trim();
                //check if the otp fits the requirements. if fits then authenticate the otp.
                if (otp.isEmpty() || otp.length() < 6){
                    verificationCode.setText("");
                    Toast.makeText(PhoneAuthenticationActivity.this, "Please type in your code again", Toast.LENGTH_LONG).show();
                }else{
                    authentication(otp);
                }
            }
        });
    }

    //using to send the otp to the phone number
    private void sendCodeToPhone(String phone){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 10, TimeUnit.SECONDS, this, mCallBacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        //Automatically authentication without the user type in the received otp
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String otp = phoneAuthCredential.getSmsCode();
            if (otp != null){
                verificationCode.setText(otp);
                authentication(otp);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneAuthenticationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    //function to try if the otp we received can sign in firebase successfully
    private void authentication(String otp) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otp);
        signWithCredential(phoneAuthCredential);
    }

    //become a registered user through phone authentication
    private void signWithCredential(PhoneAuthCredential phoneAuthCredential){
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PhoneAuthenticationActivity.this, "sign in successfully", Toast.LENGTH_SHORT).show();
                    updateNewUser();
                }else{
                    Toast.makeText(PhoneAuthenticationActivity.this, "sign in unsuccessfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Update the information to the Firebase Realtime Database when the user can become a registered user
    private void updateNewUser(){
        localUserViewModel = ViewModelProviders.of(PhoneAuthenticationActivity.this).get(LocalUserViewModel.class);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        assert firebaseUser != null;
        final String userid = firebaseUser.getUid();

        if (user_type.equals("Individual")) {
            reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = 0;
                    //check if the current phone number has been registered in the online database or not.
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        assert user != null;
                        if (user.getUsername().equals(phonenumber)) {
                            i += 1;
                        }
                    }
                    //if the information of the registered user is not in the online database
                    if (i == 0) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", phonenumber);

                        //add the information of the registered Individual user to the online database
                        reference.child(userid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PhoneAuthenticationActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PhoneAuthenticationActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(PhoneAuthenticationActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PhoneAuthenticationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }else if (user_type.equals("Official")){
            reference = FirebaseDatabase.getInstance().getReference("User_Official");
            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int i = 0;
                    //check if the current phone number has been registered in the online database or not.
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User_Official user = dataSnapshot.getValue(User_Official.class);
                        assert user != null;
                        if (user.getUsername().equals(phonenumber)) {
                            i += 1;
                        }
                    }

                    //if the information of the registered user is not in the online database
                    if (i == 0) {
                        String public_modulus = "";
                        String public_exponents = "";
                        String private_modulus = "";
                        String private_exponents = "";
                        //generate key pair for the Official user
                        try {
                            KeyPair keyPair = DigitalSignature.generateKeypair();
                            RSAPublicKey publickey = (RSAPublicKey) keyPair.getPublic();
                            RSAPrivateKey privatekey = (RSAPrivateKey) keyPair.getPrivate();

                            public_modulus = publickey.getModulus().toString();
                            public_exponents = publickey.getPublicExponent().toString();

                            private_modulus = privatekey.getModulus().toString();
                            private_exponents = privatekey.getPrivateExponent().toString();

                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("username", phonenumber);
                        hashMap.put("PublicKey_Modulus", public_modulus);
                        hashMap.put("PublicKey_exponent", public_exponents);

                        HashMap<String, Object> hashMap_chat = new HashMap<>();
                        hashMap_chat.put("sender", phonenumber);
                        hashMap_chat.put("receiver", "everyone");
                        hashMap_chat.put("status", "from_Official");
                        hashMap_chat.put("seed", "(" + public_modulus + ")" + public_exponents);

                        //store the private key into the local database
                        LocalUser officialuser = new LocalUser(phonenumber, private_modulus, private_exponents, "Official");
                        localUserViewModel.addLocalUser(officialuser);

                        databaseReference.child(phonenumber).setValue(hashMap_chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(PhoneAuthenticationActivity.this, "Public key uploaded", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(PhoneAuthenticationActivity.this, "Public key not uploaded", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        //add the information of the registered Official user to the online database
                        reference.child(userid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(PhoneAuthenticationActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(PhoneAuthenticationActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    } else {
                        localUserViewModel.updateUserStatus(phonenumber, "Official");
                        Toast.makeText(PhoneAuthenticationActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PhoneAuthenticationActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}