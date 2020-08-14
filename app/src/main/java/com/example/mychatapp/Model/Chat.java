package com.example.mychatapp.Model;

//using to store the sent message's information in online database
public class Chat {

    private String sender;

    private String receiver;

    //to define whether if this message has already been load.
    private String status;

    //for authentication
    private String secret;


    public Chat(String sender, String receiver, String status, String secret){
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.secret = secret;
    }

    public Chat(){}

    public String getSender() { return sender; }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() { return receiver; }

    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
