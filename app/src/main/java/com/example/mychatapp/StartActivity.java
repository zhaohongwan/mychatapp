package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mychatapp.Dropdownlist.AreaCode;
import com.example.mychatapp.Dropdownlist.Usertype;

public class StartActivity extends AppCompatActivity {

    private AppCompatButton button_update;
    private EditText phonenumber;
    private Spinner spinner_countrycode;
    private Spinner spinner_usertype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        button_update = findViewById(R.id.button_update);

        //create spinner drop down list for area code
        //spinner_countrycode = findViewById(R.id.spinner_countrycode);
        //spinner_countrycode.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, AreaCode.countries));

        //create spinner drop down list for user type
        spinner_usertype = findViewById(R.id.spinner_usertype);
        spinner_usertype.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Usertype.user_type));

        phonenumber = findViewById(R.id.phonenumber);

        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the phone number and selected area code
                //String phone_number = phonenumber.getText().toString().trim();
                //String area_code = AreaCode.areacode[spinner_countrycode.getSelectedItemPosition()];
                //get the select user type(Individual or Official)
                String usertype = Usertype.user_type[spinner_usertype.getSelectedItemPosition()];

                //use the area code with the phone number
                String result = CurrentNumber();
                Intent intent = new Intent(getApplicationContext(), PhoneAuthenticationActivity.class);
                //send the complete phone number and the user type(Individual or Official) to the PhoneAuthenticationActivity
                intent.putExtra("result", result);
                intent.putExtra("usertype", usertype);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), PreStartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
}
