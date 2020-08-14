package com.example.mychatapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class LocalUser {
    @PrimaryKey
    @NonNull
    private String phonenumber;

    private String private_modulus;

    private String private_exponent;

    private String status;

    public LocalUser(String phonenumber, String private_modulus, String private_exponent, String status) {
        this.phonenumber = phonenumber;
        this.private_modulus = private_modulus;
        this.private_exponent = private_exponent;
        this.status = status;
    }

    @Ignore
    public LocalUser() {
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getPrivate_modulus() {
        return private_modulus;
    }

    public void setPrivate_modulus(String private_modulus) {
        this.private_modulus = private_modulus;
    }

    public String getPrivate_exponent() {
        return private_exponent;
    }

    public void setPrivate_exponent(String private_exponent) {
        this.private_exponent = private_exponent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
