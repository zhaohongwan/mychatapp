package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychatapp.Adapter.MessageAdapter;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.Model.LocalUser;
import com.example.mychatapp.Model.User_Official;
import com.example.mychatapp.SMS.SMSBroadcastReceiver;
import com.example.mychatapp.generator.DigitalSignature;
import com.example.mychatapp.generator.HOTP;
import com.example.mychatapp.generator.Hash_salt;
import com.example.mychatapp.viewmodel.LocalchatViewModel;
import com.example.mychatapp.viewmodel.LocalUserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

//prepare for the chatting
public class MessageActivity extends AppCompatActivity{

    CircleImageView profile_icon;
    TextView username;
    RecyclerView recyclerView;
    Intent intent;

    FirebaseUser firebaseUser;

    //send button and the input text message.
    ImageButton chatbox_send_button;
    EditText chatbox_edittext;

    MessageAdapter messageAdapter;

    //using to insert received SMS into Room database
    LocalchatViewModel localchatViewModel;

    //using the extract the current user's private key
    LocalUserViewModel localUserViewModel;
    LocalUser luser;

    String usertype;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.recyclerView_message);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_icon = findViewById(R.id.profile_icon);
        username = findViewById(R.id.username);
        chatbox_send_button = findViewById(R.id.chatbox_send_button);
        chatbox_edittext = findViewById(R.id.chatbox_edittext);

        //get and set the selected user's phone number
        intent = getIntent();
        final String uname = intent.getStringExtra("username");
        username.setText(uname);

        //check if the user exists and download its imageurl.
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert uname != null;

        //get the phone number of the current device.
        final String linenumber = CurrentNumber();

        //figure out the current user's user type
        localUserViewModel = ViewModelProviders.of(MessageActivity.this).get(LocalUserViewModel.class);
        localUserViewModel.getAll().observe(MessageActivity.this, new Observer<List<LocalUser>>() {
            @Override
            public void onChanged(List<LocalUser> localUser_s) {
                int i = 0;
                for (LocalUser localuser : localUser_s){
                    if (localuser.getPhonenumber().equals(firebaseUser.getPhoneNumber()) && localuser.getStatus().equals("Official")){
                        usertype = "Official";
                        luser = localuser;
                    }else if (localuser.getPhonenumber().equals(firebaseUser.getPhoneNumber()) && localuser.getStatus().equals("None")){
                        usertype = "Individual";
                        luser = localuser;
                    }
                }
            }
        });

        localchatViewModel = ViewModelProviders.of(MessageActivity.this).get(LocalchatViewModel.class);
        localchatViewModel.loadAllChats().observe(MessageActivity.this, new Observer<List<Localchat>>() {
            @Override
            public void onChanged(List<Localchat> lcs) {
                readMessage(linenumber, uname);
            }
        });

        //set the sending function for the sending button.
        chatbox_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = chatbox_edittext.getText().toString();
                if(!msg.equals("")){
                    sendMessage(linenumber, uname, msg, usertype);
                    Toast.makeText(MessageActivity.this, "Message sent!!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MessageActivity.this, "You can not send a empty message", Toast.LENGTH_SHORT).show();
                }
                chatbox_edittext.setText("");
            }
        });

        //setting tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        //the return button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //set return button to the main activity.
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new added
                startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }

    //funciton for sending message
    private void sendMessage(final String sender, final String receiver, final String message, final String usertype){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;

        //using to store the sent message(send by the current user) in the local database
        final Localchat localchat = new Localchat();

        //if the current user is a registered user
        if (!firebaseUser.isAnonymous()){
            //if the current user is an Official user, using private key to sign the the hashed message. Then send the original message and the digital signature to the recipient.
            if (usertype.equals("Official")){
                //upload public key to the online database
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User_Official");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User_Official user = dataSnapshot.getValue(User_Official.class);
                            assert user != null;
                            if (user.getUsername().equals(sender)) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
                                HashMap<String, Object> hashMap_chat = new HashMap<>();
                                hashMap_chat.put("sender", sender);
                                hashMap_chat.put("receiver", "everyone");
                                hashMap_chat.put("status", "from_Official");
                                hashMap_chat.put("seed", "(" + luser.getPrivate_modulus() + ")" + user.getPublicKey_exponent());
                                databaseReference.child(sender).updateChildren(hashMap_chat);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

                //using private key to sign the original message
                String digtialSignature = "";
                try {
                    digtialSignature = DigitalSignature.GenerateSignature(message, luser.getPrivate_modulus(), luser.getPrivate_exponent());
                } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
                //send sms to the recipient
                String msg = "(" + message + ")" + digtialSignature;
                sendSMS(receiver, msg);

                //saved the message we sent in the local database(for the UI performance)
                localchat.setTxt_message(message);
                localchat.setSender(sender);
                localchat.setReceiver(receiver);
                localchat.setIdentity("Unknown");
                localchat.setStatus("unchecked");
                localchatViewModel.addChat(localchat);

            }else if (usertype.equals("Individual")){
                //if the current user is Individual user, then use the HOTP value as salt in the hashed message. Then send the original message, counter and the salted hashtag to the recipient.
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
                //random generate seed and counter
                long counter = HOTP.Counter(10);
                String c = String.valueOf(counter);
                //convert the byte array to String value
                byte[] seed = HOTP.Seed(10);
                String s = new String(seed);
                //generate HOTP value
                String hotp = HOTP.generateOTP(seed, counter);

                //Using the generated HOTP as the salt to add in the hashed message.
                String hashtag = Hash_salt.Hash_addSalt(message, hotp);

                //the SMS message that will sent
                String msg = "(" + message + ")" + "(" + c + ")" + hashtag;

                //hashmap the model's value, so it can be easily update to the database.
                final HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("sender", sender);
                hashMap.put("receiver", receiver);
                hashMap.put("status", "un_used");
                hashMap.put("secret", s);
                //update this single message info into the firebase database
                reference.child(firebaseUser.getPhoneNumber()).updateChildren(hashMap);

                //send sms to the recipient
                sendSMS(receiver, msg);

                //saved the message we sent in the local database(for the UI performance)
                localchat.setTxt_message(message);
                localchat.setSender(sender);
                localchat.setReceiver(receiver);
                localchat.setIdentity("Unknown");
                localchat.setStatus("unchecked");
                localchatViewModel.addChat(localchat);
            }
        }else{
            //send sms to the recipient
            sendSMS(receiver, message);
            //saved the message we sent in the local database
            localchat.setTxt_message(message);
            localchat.setSender(sender);
            localchat.setReceiver(receiver);
            localchat.setIdentity("Unknown");
            localchat.setStatus("unchecked");
            localchatViewModel.addChat(localchat);
        }
    }

    private void sendSMS(String receiver, String message){
        //create sms object for sms sending
        SMSBroadcastReceiver sms = new SMSBroadcastReceiver();
        sms.sendSMS(receiver, message);
    }

//*******************************************************************************************************************************

    private void readMessage(final String id_mine, final String id_user) {
        //building MVVM for local database(by using the constructor)
        final List<Localchat> localchats = new ArrayList<>();
        localchatViewModel = ViewModelProviders.of(MessageActivity.this).get(LocalchatViewModel.class);

        messageAdapter = new MessageAdapter();
        recyclerView.setAdapter(messageAdapter);

        localchatViewModel.loadAllChats().observe(MessageActivity.this, new Observer<List<Localchat>>() {
            @Override
            public void onChanged(List<Localchat> locs) {
                localchats.clear();
                for (final Localchat l : locs){
                    if (l.getSender().equals(id_user) && l.getReceiver().equals(id_mine)) {
                        if (l.getTxt_message().contains("(") && l.getTxt_message().contains(")") && !l.getStatus().equals("checked") && !l.getSeed().equals("")) {
                            //separate original message, hashtag and counter into three values.
                            int startIndex_message = l.getTxt_message().indexOf("(");
                            int endIndex_message = l.getTxt_message().indexOf(")");

                            String body = l.getTxt_message().substring(startIndex_message + 1, endIndex_message);
                            String rest = l.getTxt_message().replace("(" + body + ")", "");

                            //if the message is from an Individual user
                            //In the received SMS message, if we extract the original message, then the length of the remaining message is 76 characters long:
                            //76 = '(' + counter(10 characters) + ')' + salted_hashtag(64 characters) = 1 + 10 + 1 + 64 = 76 characters
                            if (rest.length() == 76){
                                int startIndex_counter = rest.indexOf("(");
                                int endIndex_counter = rest.indexOf(")");

                                String counter = rest.substring(startIndex_counter + 1, endIndex_counter);
                                long c = Long.parseLong(counter);
                                String hash = rest.replace("(" + counter + ")", "");

                                String hotp, hashtag = null;

                                hotp = HOTP.generateOTP(l.getSeed().getBytes(), c);
                                hashtag = Hash_salt.Hash_addSalt(body, hotp);

                                localchatViewModel.updateMessage(l.getConversation_id(), body);

                                //compare the new_HOTP with the HOTP that get from the firebase.
                                assert hashtag != null;
                                if (hashtag.equals(hash)) {
                                    localchatViewModel.updateIdentity(l.getConversation_id(), "Verified");
                                } else {
                                    localchatViewModel.updateIdentity(l.getConversation_id(), "Spoofed");
                                }
                                localchatViewModel.updateStatus(l.getConversation_id(), "checked");

                            //If the message is from an Official user
                            //the length of the hexadecimal digital signature is 512 characters long.
                            }else if (rest.length() == 512){
                                //digital signature = rest; body = message; l.getSeed() = public key
                                String publickey = l.getSeed();
                                //separate modulus and exponent
                                int startIndex = publickey.indexOf("(");
                                int endIndex = publickey.indexOf(")");
                                String modulus = publickey.substring(startIndex+1, endIndex);
                                String exponent = publickey.replace("(" + modulus + ")", "");

                                //convert String to BigInteger
                                BigInteger m = new BigInteger(modulus);
                                BigInteger e = new BigInteger(exponent);

                                //using modulus and exponent to regenerate public or private key.
                                RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);
                                KeyFactory factory;
                                try {
                                    factory = KeyFactory.getInstance("RSA");
                                    PublicKey pub = factory.generatePublic(spec);

                                    //initializing signature
                                    Signature signature = Signature.getInstance("SHA256withRSA");
                                    signature.initVerify(pub);
                                    signature.update(body.getBytes());

                                    //verify the digital signature
                                    byte[] sign = DigitalSignature.toByteArray(rest);
                                    boolean result = signature.verify(sign);

                                    localchatViewModel.updateMessage(l.getConversation_id(), body);

                                    if (result) {
                                        localchatViewModel.updateIdentity(l.getConversation_id(), "Verified");
                                    } else {
                                        localchatViewModel.updateIdentity(l.getConversation_id(), "Spoofed");
                                    }
                                    localchatViewModel.updateStatus(l.getConversation_id(), "checked");
                                    localchatViewModel.updateSeed(l.getConversation_id(), "");

                                } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    localchats.add(l);
                }
                messageAdapter.setLocalChat(localchats, id_mine, id_user);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.delete:
                // Add a toast just for confirmation
                Toast.makeText(this, "Clearing the data...",
                        Toast.LENGTH_SHORT).show();

                // Delete the existing data
                LocalchatViewModel.deleteAll();
                break;
            case R.id.showAll:
                startActivity(new Intent(MessageActivity.this, ListLocalActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission not Granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == 1001){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission not Granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        if (requestCode == 1002){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Permission not Granted!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    //read the current mobile device's phone number
    private String CurrentNumber(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        assert telephonyManager != null;
        //check whether the current device has the permission or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_NUMBERS}, 1001);
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1002);
        }
        //get the phone number of the current device.
        return telephonyManager.getLine1Number();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(getApplicationContext(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        return super.onKeyDown(keyCode, event);
    }

}
