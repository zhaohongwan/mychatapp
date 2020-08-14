package com.example.mychatapp.Model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class ChattedUser {
    @PrimaryKey(autoGenerate = true)
    int chat_id;

    String phonenumber;

    public ChattedUser(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    @Ignore
    public ChattedUser(){}

    public int getChat_id() {
        return chat_id;
    }

    public void setChat_id(int chat_id) {
        this.chat_id = chat_id;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
