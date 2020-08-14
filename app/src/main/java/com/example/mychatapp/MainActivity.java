package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.example.mychatapp.Fragment.OptionFragment;
import com.example.mychatapp.Fragment.ProfileFragment;
import com.example.mychatapp.Fragment.UserFragment;
import com.example.mychatapp.Model.ChattedUser;
import com.example.mychatapp.Model.Localchat;
import com.example.mychatapp.viewmodel.ChattedUserViewModel;
import com.example.mychatapp.viewmodel.LocalchatViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    LocalchatViewModel localchatViewModel;
    ChattedUserViewModel chattedUserViewModel;

    //broadcast receiver to receive the Sender ID and the content of the incoming message
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null){
                //get the current device's phone number
                String number = CurrentNumber();

                //add this message to the local database
                addToData(intent.getStringExtra("sender"), number, intent.getStringExtra("message"), "unchecked", intent.getStringExtra("seed"));

                //set this sender to a chatted phone number in the local database
                addToNum(intent.getStringExtra("sender"));

                Toast.makeText(context, intent.getStringExtra("sender") + ", " + number + ", " + intent.getStringExtra("message") + ", " + "Unknown" + "," + "unchecked", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //register sms broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("SMS_RECEIVED");
        registerReceiver(broadcastReceiver, filter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //set bundle to sender the current device's phone number to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("phonenumber", CurrentNumber());
        Fragment user_fragment = new UserFragment();
        Fragment profile_fragment = new ProfileFragment();
        user_fragment.setArguments(bundle);
        profile_fragment.setArguments(bundle);

        viewPagerAdapter.addFragment(user_fragment, "Users");

        if (Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isAnonymous()){
            //if the current user is an anonymous user, present the fragment that has the option to upgrade the current account to a registered account.
            viewPagerAdapter.addFragment(new OptionFragment(), "Options");
        }else{
            //This fragment will display the current user's phone number and an option for him to log out the account.
            viewPagerAdapter.addFragment(profile_fragment, "Profiles");
        }

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    //build adapter for the viewpager
    private static class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    //get the current phone number
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

    //return the result of the permission checking
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

    //add the received message to the message table that stored in the local database
    private void addToData(String sender, String receiver, String message, String status, String seed){
        final Localchat localchat = new Localchat();
        localchatViewModel = ViewModelProviders.of(MainActivity.this).get(LocalchatViewModel.class);
        localchat.setSender(sender);
        localchat.setReceiver(receiver);
        localchat.setTxt_message(message);
        localchat.setIdentity("Unknown");
        localchat.setStatus(status);
        localchat.setSeed(seed);
        localchatViewModel.addChat(localchat);
    }

    //add the received message's phone number to the Chatted_user table in the local database
    private void addToNum(final String phonenumber){
        final ChattedUser user = new ChattedUser();
        //create the chatted_user view-model to manipulate the local database
        chattedUserViewModel = ViewModelProviders.of(MainActivity.this).get(ChattedUserViewModel.class);

        //load all of the data in chatted_user table
        chattedUserViewModel.loadAllUsers().observe(MainActivity.this, new Observer<List<ChattedUser>>() {
            @Override
            public void onChanged(List<ChattedUser> chattedUsers) {
                int i = 0;
                for (ChattedUser chattedUser : chattedUsers){
                    //check if the local database already has the phone number.
                    if (chattedUser.getPhonenumber().equals(phonenumber)){
                        i += 1;
                    }
                }
                //if the local database does not have any matched phone number, then add it to the Chatted_user.
                if (i == 0){
                    user.setPhonenumber(phonenumber);
                    chattedUserViewModel.addUser(user);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregistered the broadcast receiver when the activity destroyed.
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //make sure the current activity can not be stopped.
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(MainActivity.this, "Exit by press the HOME button", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
