package com.example.mychatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mychatapp.Dropdownlist.AreaCode;
import com.example.mychatapp.Model.ChattedUser;
import com.example.mychatapp.viewmodel.ChattedUserViewModel;

import java.util.List;

//using to create communication with other phone number.
public class AddNewContactActivity extends AppCompatActivity {

    private AppCompatButton add_button;
    private EditText phonenumber;
    private Spinner spinner_countrycode;
    ChattedUserViewModel chattedUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_contact);

        add_button = findViewById(R.id.add_button);

        //create spinner drop down list for area code
        spinner_countrycode = findViewById(R.id.spinner_countrycode);
        spinner_countrycode.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, AreaCode.countries));

        phonenumber = findViewById(R.id.phonenumber);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the phone number and selected area code
                String phone_number = phonenumber.getText().toString().trim();
                String area_code = AreaCode.areacode[spinner_countrycode.getSelectedItemPosition()];

                if (TextUtils.isEmpty(phone_number)){
                    phonenumber.setText("");
                    Toast.makeText(AddNewContactActivity.this, "Please enter the phone number", Toast.LENGTH_SHORT).show();
                }else{
                    //use the area code with the phone number
                    String result = "+" + area_code + phone_number;
                    addToNum(result);
                    Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                    intent.putExtra("username", result);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }

    //add the received message's phone number to the Chatted_user table in the local database
    private void addToNum(final String phonenumber){
        final ChattedUser user = new ChattedUser();
        //create the chatted_user view-model to manipulate the local database
        chattedUserViewModel = ViewModelProviders.of(AddNewContactActivity.this).get(ChattedUserViewModel.class);

        //load all of the data in chatted_user table
        chattedUserViewModel.loadAllUsers().observe(AddNewContactActivity.this, new Observer<List<ChattedUser>>() {
            @Override
            public void onChanged(List<ChattedUser> chattedUsers) {
                int i = 0;
                for (ChattedUser chattedUser : chattedUsers){
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
}