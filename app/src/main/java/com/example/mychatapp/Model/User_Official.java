package com.example.mychatapp.Model;

//Building Encapsulation and Constructor for the user_official class
public class User_Official {
    private String id;
    private String username;
    private String PublicKey_modulus;
    private String PublicKey_exponent;

    public User_Official(String id, String username, String publicKey_modulus, String publicKey_exponent) {
        this.id = id;
        this.username = username;
        this.PublicKey_modulus = publicKey_modulus;
        this.PublicKey_exponent = publicKey_exponent;
    }

    public User_Official() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPublicKey_modulus() {
        return PublicKey_modulus;
    }

    public void setPublicKey_modulus(String publicKey_modulus) {
        this.PublicKey_modulus = publicKey_modulus;
    }

    public String getPublicKey_exponent() {
        return PublicKey_exponent;
    }

    public void setPublicKey_exponent(String publicKey_exponent) {
        this.PublicKey_exponent = publicKey_exponent;
    }
}
