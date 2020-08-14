package com.example.mychatapp.Model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

//Create the table for storing messages
@Entity
public class Localchat {

    @PrimaryKey(autoGenerate = true)
    private int conversation_id;

    private String txt_message;

    private String sender;

    private String receiver;

    private String identity;

    private String status;

    private String seed;

    //building constructor
    public Localchat(String txt_message, String sender, String receiver, String identity, String status, String seed) {
        this.txt_message = txt_message;
        this.sender = sender;
        this.receiver = receiver;
        this.identity = identity;
        this.status = status;
        this.seed = seed;
    }

    @Ignore
    public Localchat(){}

    //***************************************
    public int getConversation_id() {
        return conversation_id;
    }

    public void setConversation_id(int conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getTxt_message() {
        return txt_message;
    }

    public void setTxt_message(String txt_message) {
        this.txt_message = txt_message;
    }

    public String getSender() { return sender; }

    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }

    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getIdentity() { return identity; }

    public void setIdentity(String identity) { this.identity = identity; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getSeed() { return seed; }

    public void setSeed(String seed) { this.seed = seed; }
}
